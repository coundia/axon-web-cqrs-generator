package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.domainservice.EventGeneratorService;
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
public class EventTests {

    @Autowired
    EventGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_event_files(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("event.mustache"),
                "package test;\n\npublic class {{name}}{{eventType}}Event({{#fields}}{{type}} {{name}}{{^last}}, {{/last}}{{/fields}}) {}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        for (String type : new String[]{"Created", "Updated", "Deleted"}) {
            File file = tempDir.resolve(generatorProperties.getEventPackage() + "/MockEntity" + type + "Event.java").toFile();
            assertThat(file).exists();

            String content = Files.readString(file.toPath());
            assertThat(content).contains("public class MockEntity" + type + "Event");
        }
    }
}
