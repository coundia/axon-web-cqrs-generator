package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.presentationservice.UpdateControllerGeneratorService;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
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
public class UpdateControllerTests {

    @Autowired
    UpdateControllerGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_update_controller_file(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("updateController.mustache"),
                "package {{package}};\n\npublic class Update{{name}}Controller {}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        File generated = tempDir.resolve(generatorProperties.getControllerPackage() + "/UpdateMockEntityController.java").toFile();
        assertThat(generated).exists();

        String content = Files.readString(generated.toPath());
        assertThat(content).contains("public class UpdateMockEntityController");
    }
}
