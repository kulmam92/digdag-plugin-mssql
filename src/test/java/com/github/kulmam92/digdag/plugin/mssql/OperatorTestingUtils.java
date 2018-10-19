package com.github.kulmam92.digdag.plugin.mssql;

import com.google.common.base.Optional;
import io.digdag.core.agent.GrantedPrivilegedVariables;
import io.digdag.spi.ImmutableTaskRequest;
import io.digdag.spi.OperatorContext;
import io.digdag.spi.PrivilegedVariables;
import io.digdag.spi.SecretProvider;
import io.digdag.spi.TaskRequest;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Properties;
import java.util.UUID;

import static com.github.kulmam92.digdag.plugin.mssql.ConfigUtils.newConfig;

public class OperatorTestingUtils
{
    public static ImmutableTaskRequest newTaskRequest()
    {
        return ImmutableTaskRequest.builder()
                .siteId(1)
                .projectId(2)
                .workflowName("wf")
                .revision(Optional.of("rev"))
                .taskId(3)
                .attemptId(4)
                .sessionId(5)
                .taskName("t")
                .lockId("l")
                .timeZone(ZoneId.systemDefault())
                .sessionUuid(UUID.randomUUID())
                .sessionTime(Instant.now())
                .createdAt(Instant.now())
                .config(newConfig())
                .localConfig(newConfig())
                .lastStateParams(newConfig())
                .build();
    }
    public static TestingOperatorContext newContext(Path projectPath, TaskRequest request)
    {
        return new TestingOperatorContext(
                projectPath,
                request,
                TestingSecretProvider.empty(),
                GrantedPrivilegedVariables.empty());
    }

    public static class TestingOperatorContext
            implements OperatorContext
    {
        private final Path projectPath;
        private final TaskRequest taskRequest;
        private final SecretProvider secrets;
        private final PrivilegedVariables privilegedVariables;

        public TestingOperatorContext(
                Path projectPath,
                TaskRequest taskRequest,
                SecretProvider secrets,
                PrivilegedVariables privilegedVariables)
        {
            this.projectPath = projectPath;
            this.taskRequest = taskRequest;
            this.secrets = secrets;
            this.privilegedVariables = privilegedVariables;
        }

        @Override
        public Path getProjectPath()
        {
            return projectPath;
        }

        @Override
        public TaskRequest getTaskRequest()
        {
            return taskRequest;
        }

        @Override
        public SecretProvider getSecrets()
        {
            return secrets;
        }

        @Override
        public PrivilegedVariables getPrivilegedVariables()
        {
            return privilegedVariables;
        }
    }

    private static class TestingSecretProvider
            implements SecretProvider
    {
        public static TestingSecretProvider empty()
        {
            return fromProperties(new Properties());
        }

        public static TestingSecretProvider fromProperties(Properties props)
        {
            return new TestingSecretProvider(props);
        }

        private final Properties props;

        private TestingSecretProvider(Properties props)
        {
            this.props = props;
        }

        public Optional<String> getSecretOptional(String key)
        {
            return Optional.fromNullable(props.getProperty(key));
        }
    }

}
