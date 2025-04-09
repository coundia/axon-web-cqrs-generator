package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.applicationservice.DtoRequestGeneratorService;
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
public class DtoRequestTests {

    @Autowired
    DtoRequestGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_dto_request_file(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("dtoRequest.mustache"),
                "package test;\n\npublic record {{name}}Request({{#fields}}{{type}} {{name}}{{^last}}, {{/last}}{{/fields}}) {}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        File file = tempDir.resolve(generatorProperties.getDtoPackage() + "/MockEntityRequest.java").toFile();
        assertThat(file).exists();

        String content = Files.readString(file.toPath());
        assertThat(content).contains("public class MockEntityRequest");
    }
}
