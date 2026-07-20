package com.eiou.spring.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boot")
public class BootApiController {
    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final DemoProperties demoProperties;
    private final DemoFeature demoFeature;

    @Value("${demo.message}")
    private String message;

    public BootApiController(ApplicationContext applicationContext,
                             Environment environment,
                             DemoProperties demoProperties,
                             DemoFeature demoFeature) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.demoProperties = demoProperties;
        this.demoFeature = demoFeature;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name", defaultValue = "Spring Boot") String name) {
        return "hello " + name + ", " + message;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return Map.of(
                "spring.application.name", environment.getProperty("spring.application.name", ""),
                "server.port", environment.getProperty("server.port", "8080"),
                "demo.message.fromValue", message,
                "demo.message.fromConfigurationProperties", demoProperties.getMessage()
        );
    }

    @GetMapping("/properties")
    public Map<String, Object> properties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("message", demoProperties.getMessage());
        properties.put("feature", Map.of(
                "enabled", demoProperties.getFeature().isEnabled(),
                "mode", demoProperties.getFeature().getMode()
        ));
        properties.put("client", Map.of(
                "baseUrl", demoProperties.getClient().getBaseUrl(),
                "timeout", demoProperties.getClient().getTimeout().toString()
        ));
        properties.put("items", demoProperties.getItems());
        properties.put("labels", demoProperties.getLabels());
        return properties;
    }

    @GetMapping("/profile")
    public Map<String, Object> profile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Map.of(
                "activeProfiles", activeProfiles.length == 0 ? List.of("default") : Arrays.asList(activeProfiles),
                "defaultProfiles", Arrays.asList(environment.getDefaultProfiles()),
                "demo.message", demoProperties.getMessage(),
                "demo.feature.enabled", demoProperties.getFeature().isEnabled()
        );
    }

    @GetMapping("/condition")
    public Map<String, Object> condition() {
        return Map.of(
                "demo.feature.enabled", demoProperties.getFeature().isEnabled(),
                "demoFeature", demoFeature.description(),
                "enabledDemoFeatureBean", applicationContext.containsBean("enabledDemoFeature"),
                "defaultDemoFeatureBean", applicationContext.containsBean("defaultDemoFeature")
        );
    }

    @GetMapping("/priority")
    public Map<String, Object> priority() {
        return Map.of(
                "observed.demo.message", demoProperties.getMessage(),
                "observed.server.port", environment.getProperty("server.port", "8080"),
                "commonPrecedenceHighToLow", List.of(
                        "command line arguments: --demo.message=...",
                        "JVM system properties: -Ddemo.message=...",
                        "OS environment variables: DEMO_MESSAGE=...",
                        "profile config: application-dev.yml",
                        "default config: application.yml",
                        "code defaults"
                )
        );
    }

    @GetMapping("/auto-config")
    public Map<String, Object> autoConfig() {
        return Map.of(
                "applicationName", applicationContext.getApplicationName(),
                "dispatcherServlet", applicationContext.containsBean("dispatcherServlet"),
                "requestMappingHandlerMapping", applicationContext.containsBean("requestMappingHandlerMapping"),
                "characterEncodingFilter", applicationContext.containsBean("characterEncodingFilter"),
                "jacksonObjectMapper", applicationContext.containsBean("jacksonObjectMapper"),
                "tomcatServletWebServerFactory", applicationContext.containsBean("tomcatServletWebServerFactory"),
                "environmentType", environment.getClass().getSimpleName()
        );
    }
}
