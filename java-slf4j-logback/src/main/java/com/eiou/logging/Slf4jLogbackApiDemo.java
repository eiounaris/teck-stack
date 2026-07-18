package com.eiou.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class Slf4jLogbackApiDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jLogbackApiDemo.class);

    private Slf4jLogbackApiDemo() {
    }

    public static void main(String[] args) {
        logLevels();
        placeholders();
        exceptionLogging();
        mappedDiagnosticContext();
        fluentApi();
    }

    private static void logLevels() {
        LOGGER.trace("trace message");
        LOGGER.debug("debug message");
        LOGGER.info("info message");
        LOGGER.warn("warn message");
        LOGGER.error("error message");
    }

    private static void placeholders() {
        LOGGER.info("hello, {}", "SLF4J");
        LOGGER.info("name={}, count={}", "demo", 3);
    }

    private static void exceptionLogging() {
        try {
            throw new IllegalStateException("demo exception");
        } catch (IllegalStateException exception) {
            LOGGER.error("exception message", exception);
        }
    }

    private static void mappedDiagnosticContext() {
        MDC.put("traceId", "TRACE-001");
        try {
            LOGGER.info("message with traceId");
        } finally {
            MDC.remove("traceId");
        }
    }

    private static void fluentApi() {
        LOGGER.atInfo()
                .addKeyValue("style", "fluent")
                .log("message from fluent api");
    }
}
