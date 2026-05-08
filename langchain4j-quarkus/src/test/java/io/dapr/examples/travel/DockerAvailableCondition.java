package io.dapr.examples.travel;

import java.io.File;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 condition that disables tests when Docker is not available.
 * Prevents Dapr Testcontainers-based integration tests from failing
 * in environments without Docker.
 */
public class DockerAvailableCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (new File("/var/run/docker.sock").exists()) {
            return ConditionEvaluationResult.enabled("Docker socket found at /var/run/docker.sock");
        }

        String home = System.getProperty("user.home");
        if (home != null && new File(home + "/.docker/run/docker.sock").exists()) {
            return ConditionEvaluationResult.enabled("Docker socket found at ~/.docker/run/docker.sock");
        }

        try {
            Process process = new ProcessBuilder("docker", "info")
                    .redirectErrorStream(true)
                    .start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ConditionEvaluationResult.enabled("Docker is available (docker info succeeded)");
            }
        } catch (Exception e) {
            // docker command not found or failed
        }

        return ConditionEvaluationResult.disabled("Docker is not available, skipping integration test");
    }
}
