package com.groupe2cs.generator.tests.application.services;

import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.tests.infrastrucutre.config.GeneratorPropertiesTestConfig;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.application.service.applicationservice.CommandGeneratorService;
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
public class CommandTests {

    @Autowired
    CommandGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_command_files(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(templatesDir.resolve("command.mustache"),
                "package {{package}};\n\n" +
                        "{{#imports}}\nimport {{.}};\n{{/imports}}\n" +
                        "import java.io.Serializable;\n" +
                        "import lombok.AllArgsConstructor;\n" +
                        "import lombok.Getter;\n" +
                        "import lombok.NoArgsConstructor;\n" +
                        "import lombok.Setter;\n" +
                        "import lombok.Builder;\n\n" +
                        "@Getter\n@Setter\n@AllArgsConstructor\n@NoArgsConstructor\n@Builder\n" +
                        "public class {{name}}Command implements Serializable {\n" +
                        "{{#fields}}  private {{type}} {{name}};\n{{/fields}}" +
                        "}\n");

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        for (String prefix : new String[]{"Create", "Update", "Delete"}) {
            String fileName = prefix + "MockEntityCommand.java";
            File generated = tempDir.resolve(generatorProperties.getCommandPackage() + "/" + fileName).toFile();
            assertThat(generated).exists();
            String content = Files.readString(generated.toPath());
            assertThat(content).contains("public class " + prefix + "MockEntityCommand");
            assertThat(content).contains(" private  MockEntityId id;");
        }
    }

    static class MockEntity {
        private String id;
        private String name;
    }
}
