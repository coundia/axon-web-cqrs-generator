package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.applicationservice.PagedResponseGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = GeneratorPropertiesTestConfig.class)
public class PagedResponseTests {

    @Autowired
    PagedResponseGeneratorService service;

    @Autowired
    GeneratorProperties properties;

    @Test
    void it_should_generate_paged_response_class(@TempDir Path tempDir) throws Exception {
        Files.createDirectories(tempDir.resolve("templates"));
        Files.writeString(tempDir.resolve("templates/pagedResponse.mustache"),
                "package {{package}};\n\npublic class PagedResponse(java.util.List<T> content, int page, int size, long totalElements, int totalPages) {}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(ListQueryHandlerGeneratorServiceTest.MockEntity.class);
        service.generate(definition,tempDir.toString());

        Path generatedFile = tempDir.resolve(properties.getDtoPackage() + "/MockEntityPagedResponse.java");
        assertThat(generatedFile.toFile()).exists();

        String content = Files.readString(generatedFile);
        assertThat(content).contains("public class MockEntityPagedResponse");
    }
}
