package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.domainservice.ExceptionGeneratorService;
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
public class ExceptionTests {

    @Autowired
    ExceptionGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_exception_file(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("exception.mustache"),
                "package test;\n\npublic class {{name}}NotFoundException extends RuntimeException {\n    public {{name}}NotFoundException({{name}}Id id) {\n        super(\"{{name}} with ID \" + id + \" not found\");\n    }\n}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        File file = tempDir.resolve(generatorProperties.getExceptionPackage() + "/MockEntityNotFoundException.java").toFile();
        assertThat(file).exists();

        String content = Files.readString(file.toPath());
        assertThat(content).contains("class MockEntityNotFoundException");
        assertThat(content).contains("super(\"MockEntity with ID \" + id + \" not found\")");
    }
}
