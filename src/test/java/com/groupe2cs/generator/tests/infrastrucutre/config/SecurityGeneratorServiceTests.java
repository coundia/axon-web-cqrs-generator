package com.groupe2cs.generator.tests.infrastrucutre.config;

import com.groupe2cs.generator.application.service.infrastructureservice.SecurityGeneratorService;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.infrastructure.entity.MockEntity;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {GeneratorPropertiesTestConfig.class})
public class SecurityGeneratorServiceTests {

    @Autowired
    SecurityGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_user_role_permission_and_config(@TempDir Path tempDir) throws Exception {
        Path fullDir = tempDir.resolve("Security");
        Files.createDirectories(fullDir);

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        definition.getStack().add("Security");

        service.generate(definition, fullDir.toString()).blockLast();

        Path entityPath = fullDir.resolve("infrastructure/entity");
        File user = entityPath.resolve("User.java").toFile();
        File role = entityPath.resolve("Role.java").toFile();
        File permission = entityPath.resolve("Permission.java").toFile();

        Path configPath = fullDir.resolve("infrastructure/config");
        File config = configPath.resolve("SecurityConfig.java").toFile();

        assertThat(user).exists();
        assertThat(role).exists();
        assertThat(permission).exists();
        assertThat(config).exists();
    }
}