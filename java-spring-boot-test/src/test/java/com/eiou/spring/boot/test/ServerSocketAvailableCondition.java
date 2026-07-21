package com.eiou.spring.boot.test;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.ServerSocket;

class ServerSocketAvailableCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        try (ServerSocket ignored = new ServerSocket(0)) {
            return ConditionEvaluationResult.enabled("server sockets are available");
        } catch (IOException | RuntimeException exception) {
            return ConditionEvaluationResult.disabled(
                    "server sockets are not available: " + exception.getMessage()
            );
        }
    }
}
