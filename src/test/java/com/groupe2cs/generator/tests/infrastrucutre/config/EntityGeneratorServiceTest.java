package com.groupe2cs.generator.tests.infrastrucutre.config;
import com.groupe2cs.generator.application.service.infrastructureservice.EntityGeneratorService;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class EntityGeneratorServiceTest {

	@Test
	void it_should_generate_entity_file() {
		TemplateEngine templateEngine = mock(TemplateEngine.class);
		FileWriterService fileWriterService = mock(FileWriterService.class);
		GeneratorProperties generatorProperties = mock(GeneratorProperties.class);

		EntityGeneratorService service = new EntityGeneratorService(templateEngine, fileWriterService, generatorProperties);

		EntityDefinition definition = EntityDefinition.builder()
				.name("Product")
				.table("products")
				.auditable(true)
				.fields(List.of(
						FieldDefinition.builder().name("name").type("String").nullable(false).unique(true).build(),
						FieldDefinition.builder().name("price").type("Double").nullable(false).unique(false).build()
				))
				.build();

		when(generatorProperties.getEntityPackage()).thenReturn("infrastructure/entity");
		when(generatorProperties.getVoPackage()).thenReturn("domain/valueobject");
		when(templateEngine.render(eq("infrastructure/entity.mustache"), any())).thenReturn("class Product {}");

		service.generate(definition, "/tmp");

		verify(fileWriterService).write(eq("/tmp/infrastructure/entity"), eq("Product.java"), eq("class Product {}"));
	}

}