package com.eiou.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Context;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public final class CookieSessionTokenApiDemo {
    private static final int DEFAULT_PORT = 8081;
    private static final String COOKIE_CLIENT_ID = "demo_client_id";
    private static final String COOKIE_VISITS = "demo_cookie_visits";
    private static final String SESSION_USER = "sessionUser";
    private static final String SESSION_LOGIN_AT = "sessionLoginAt";
    private static final Duration TOKEN_TTL = Duration.ofMinutes(15);
    private static final TokenService TOKEN_SERVICE = new TokenService("local-demo-secret-change-me");

    private CookieSessionTokenApiDemo() {
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("port", DEFAULT_PORT);
        Tomcat tomcat = startTomcat(port);

        System.out.println("Cookie + Session + Token demo started");
        System.out.println("Index: http://localhost:" + port + "/");
        System.out.println("Cookie: http://localhost:" + port + "/cookie");
        System.out.println("Session login: http://localhost:" + port + "/session/login?user=Alice");
        System.out.println("Token login: http://localhost:" + port + "/token/login?user=Alice");

        tomcat.getServer().await();
    }

    private static Tomcat startTomcat(int port) throws Exception {
        Path baseDirectory = Files.createTempDirectory("cookie-session-token-");

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDirectory.toString());
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", baseDirectory.toString());
        context.setParentClassLoader(CookieSessionTokenApiDemo.class.getClassLoader());
        context.setSessionTimeout(30);
        if (context instanceof StandardContext standardContext) {
            standardContext.setClearReferencesThreadLocals(false);
            standardContext.setClearReferencesRmiTargets(false);
        }

        Tomcat.addServlet(context, "indexServlet", new IndexServlet());
        context.addServletMappingDecoded("/", "indexServlet");
        Tomcat.addServlet(context, "cookieServlet", new CookieServlet());
        context.addServletMappingDecoded("/cookie", "cookieServlet");
        Tomcat.addServlet(context, "sessionLoginServlet", new SessionLoginServlet());
        context.addServletMappingDecoded("/session/login", "sessionLoginServlet");
        Tomcat.addServlet(context, "sessionProfileServlet", new SessionProfileServlet());
        context.addServletMappingDecoded("/session/profile", "sessionProfileServlet");
        Tomcat.addServlet(context, "sessionLogoutServlet", new SessionLogoutServlet());
        context.addServletMappingDecoded("/session/logout", "sessionLogoutServlet");
        Tomcat.addServlet(context, "tokenLoginServlet", new TokenLoginServlet());
        context.addServletMappingDecoded("/token/login", "tokenLoginServlet");
        Tomcat.addServlet(context, "tokenProfileServlet", new TokenProfileServlet());
        context.addServletMappingDecoded("/token/profile", "tokenProfileServlet");

        tomcat.start();
        return tomcat;
    }

    public static final class IndexServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            writeLines(response,
                    "Cookie + Session + Token demo",
                    "",
                    "GET /cookie",
                    "GET /session/login?user=Alice",
                    "GET /session/profile",
                    "GET /session/logout",
                    "GET /token/login?user=Alice",
                    "GET /token/profile with header: Authorization: Bearer <token>");
        }
    }

    public static final class CookieServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            Optional<Cookie> existingClientId = cookie(request, COOKIE_CLIENT_ID);
            String clientId = existingClientId.map(Cookie::getValue)
                    .orElseGet(() -> "client-" + UUID.randomUUID());
            int visits = cookie(request, COOKIE_VISITS)
                    .map(Cookie::getValue)
                    .map(CookieSessionTokenApiDemo::parsePositiveInt)
                    .orElse(0) + 1;

            response.addCookie(newCookie(COOKIE_CLIENT_ID, clientId, true));
            response.addCookie(newCookie(COOKIE_VISITS, Integer.toString(visits), false));

            writeLines(response,
                    "mode=cookie",
                    "clientId=" + clientId,
                    "clientIdWasSentByClient=" + existingClientId.isPresent(),
                    "cookieVisitCount=" + visits,
                    "note=Cookie values live on the client and are sent back with matching requests.");
        }
    }

    public static final class SessionLoginServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String user = parameter(request, "user", "session-user");
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval((int) Duration.ofMinutes(30).toSeconds());
            session.setAttribute(SESSION_USER, user);
            session.setAttribute(SESSION_LOGIN_AT, Instant.now().toString());

            writeLines(response,
                    "mode=session-login",
                    "sessionId=" + session.getId(),
                    "sessionIsNew=" + session.isNew(),
                    "user=" + user,
                    "next=Call /session/profile with the JSESSIONID cookie.");
        }
    }

    public static final class SessionProfileServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute(SESSION_USER) == null) {
                writeUnauthorized(response,
                        "mode=session-profile",
                        "error=No active session. Call /session/login?user=Alice first.");
                return;
            }

            Integer visits = (Integer) session.getAttribute("profileVisits");
            visits = visits == null ? 1 : visits + 1;
            session.setAttribute("profileVisits", visits);

            writeLines(response,
                    "mode=session-profile",
                    "sessionId=" + session.getId(),
                    "user=" + session.getAttribute(SESSION_USER),
                    "loginAt=" + session.getAttribute(SESSION_LOGIN_AT),
                    "profileVisitsInSession=" + visits,
                    "note=The browser only stores JSESSIONID; the user data stays on the server.");
        }
    }

    public static final class SessionLogoutServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            HttpSession session = request.getSession(false);
            String sessionId = session == null ? "(none)" : session.getId();
            if (session != null) {
                session.invalidate();
            }

            writeLines(response,
                    "mode=session-logout",
                    "invalidatedSessionId=" + sessionId,
                    "next=Call /session/profile again to see the 401 response.");
        }
    }

    public static final class TokenLoginServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String user = parameter(request, "user", "token-user");
            Instant expiresAt = Instant.now().plus(TOKEN_TTL);
            String token = TOKEN_SERVICE.issue(user, expiresAt);
            response.setHeader("X-Demo-Token-Type", "Bearer");

            writeLines(response,
                    "mode=token-login",
                    "user=" + user,
                    "expiresAt=" + expiresAt,
                    "token=" + token,
                    "next=Call /token/profile with header Authorization: Bearer " + token);
        }
    }

    public static final class TokenProfileServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            Optional<String> bearerToken = bearerToken(request);
            if (bearerToken.isEmpty()) {
                writeUnauthorized(response,
                        "mode=token-profile",
                        "error=Missing Authorization: Bearer <token> header.");
                return;
            }

            Optional<TokenClaims> claims = TOKEN_SERVICE.verify(bearerToken.get());
            if (claims.isEmpty()) {
                writeUnauthorized(response,
                        "mode=token-profile",
                        "error=Token is invalid, tampered with, or expired.");
                return;
            }

            writeLines(response,
                    "mode=token-profile",
                    "subject=" + claims.get().subject(),
                    "expiresAt=" + claims.get().expiresAt(),
                    "note=The server validates the signed token instead of reading server session state.");
        }
    }

    private static Cookie newCookie(String name, String value, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofHours(1).toSeconds());
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    private static Optional<Cookie> cookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    private static Optional<String> bearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return Optional.empty();
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    private static String parameter(HttpServletRequest request, String name, String fallback) {
        String value = request.getParameter(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static int parsePositiveInt(String value) {
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static void writeUnauthorized(HttpServletResponse response, String... lines) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writeLines(response, lines);
    }

    private static void writeLines(HttpServletResponse response, String... lines) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        for (String line : lines) {
            response.getWriter().println(line);
        }
    }

    private record TokenClaims(String subject, Instant expiresAt) {
    }

    private static final class TokenService {
        private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
        private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
        private static final String HMAC_SHA_256 = "HmacSHA256";
        private final byte[] secret;

        private TokenService(String secret) {
            this.secret = secret.getBytes(StandardCharsets.UTF_8);
        }

        private String issue(String subject, Instant expiresAt) {
            String encodedSubject = BASE64_URL_ENCODER.encodeToString(subject.getBytes(StandardCharsets.UTF_8));
            String unsignedToken = encodedSubject + "." + expiresAt.getEpochSecond();
            return unsignedToken + "." + sign(unsignedToken);
        }

        private Optional<TokenClaims> verify(String token) {
            String[] parts = token.split("\\.", 3);
            if (parts.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            try {
                Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(parts[1]));
                if (!Instant.now().isBefore(expiresAt)) {
                    return Optional.empty();
                }
                String subject = new String(BASE64_URL_DECODER.decode(parts[0]), StandardCharsets.UTF_8);
                return Optional.of(new TokenClaims(subject, expiresAt));
            } catch (IllegalArgumentException exception) {
                return Optional.empty();
            }
        }

        private String sign(String value) {
            try {
                Mac mac = Mac.getInstance(HMAC_SHA_256);
                mac.init(new SecretKeySpec(secret, HMAC_SHA_256));
                return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
            } catch (Exception exception) {
                throw new IllegalStateException("Unable to sign token", exception);
            }
        }
    }
}
