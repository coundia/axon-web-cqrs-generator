package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.domainservice.VoGeneratorService;
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
public class VoTests {

    @Autowired
    VoGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_vo_files(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(templatesDir.resolve("vo.mustache"), "public class {{voName}} {}");

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);

        service.generate(definition, tempDir.toString());

        File idVo = tempDir.resolve(generatorProperties.getVoPackage()+"/MockEntityId.java").toFile();
        File nameVo = tempDir.resolve(generatorProperties.getVoPackage()+"/MockEntityName.java").toFile();

        assertThat(Files.readString(idVo.toPath())).contains("public class MockEntityId");
        assertThat(Files.readString(nameVo.toPath())).contains("public class MockEntityName");
    }
}