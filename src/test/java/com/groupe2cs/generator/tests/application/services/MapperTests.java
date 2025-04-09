package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.applicationservice.MapperGeneratorService;
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
public class MapperTests {

    @Autowired
    MapperGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_mapper_file(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("mapper.mustache"),
                "package test;\n\npublic class {{name}}Mapper {\n\n    public static {{name}}Response toResponse({{name}} entity) {\n        return new {{name}}Response({{#fields}}entity.{{name}}(){{^last}}, {{/last}}{{/fields}});\n    }\n\n    public static {{name}} toEntity({{name}}Request request) {\n        return new {{name}}({{#fields}}request.{{name}}(){{^last}}, {{/last}}{{/fields}});\n    }\n}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        File file = tempDir.resolve(generatorProperties.getMapperPackage() + "/MockEntityMapper.java").toFile();
        assertThat(file).exists();

        String content = Files.readString(file.toPath());
        assertThat(content).contains("public class MockEntityMapper");
        assertThat(content).contains("toResponse");
        assertThat(content).contains("toEntity");
    }
}
