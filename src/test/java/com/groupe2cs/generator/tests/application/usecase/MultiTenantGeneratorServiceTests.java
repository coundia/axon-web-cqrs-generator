package com.groupe2cs.generator.tests.application.usecase;

import com.groupe2cs.generator.application.usecase.MultiTenantGeneratorService;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.infrastructure.entity.MockEntity;
import com.groupe2cs.generator.domain.model.EntityDefinition;
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
public class MultiTenantGeneratorServiceTests {

	@Autowired
	private MultiTenantGeneratorService service;

	@Autowired
	private GeneratorProperties generatorProperties;

	@Test
	void it_should_generate_multi_tenant_module(@TempDir Path tempDir) throws Exception {
		Path fullDir = tempDir.resolve("tenant");
		Files.createDirectories(fullDir);

		EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
		definition.setMultiTenant(true);

		service.generate(definition, fullDir.toString()).block();

		Path entityPath = fullDir.resolve("infrastructure/entity");
		File tenantEntity = entityPath.resolve("Tenant.java").toFile();
		assertThat(tenantEntity).exists();

		File TenantCreateControllerIntegrationTest = fullDir
				.resolve("presentation/controller")
				.resolve("TenantCreateControllerIntegrationTest.java").toFile();
		assertThat(TenantCreateControllerIntegrationTest).exists();

		String tenantEntityContent = Files.readString(tenantEntity.toPath());
		assertThat(tenantEntityContent).contains("class Tenant");
		assertThat(tenantEntityContent).contains("private String id;");

	}
}
