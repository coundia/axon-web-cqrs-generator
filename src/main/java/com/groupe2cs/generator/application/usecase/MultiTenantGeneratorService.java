package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class MultiTenantGeneratorService {

	private final GeneratorProperties generatorProperties;
	private final GroupMainGenerator groupMainGenerator;

	private Mono<Void> createTenantEntity(String baseDir, EntityDefinition parentEntityDefinition) {
		EntityDefinition tenant = EntityDefinition.builder()
				.name("Tenant")
				.table("tenants")
				.apiPrefix("/admin")
				.auditable(true)
				.fields(List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("name").unique(true).type("String").build(),

						FieldDefinition.builder().name("description").type("String").columnDefinition("TEXT")
								.nullable(true).build(),
						FieldDefinition.builder().name("domain").type("String").nullable(true).build(),
						FieldDefinition.builder().name("language").type("String").nullable(true).build(),
						FieldDefinition.builder().name("active").type("Boolean").nullable(true).build(),
						FieldDefinition.builder().name("tenant")
								.type("Tenant")
								.relation("ManyToOne")
								.nullable(true)
								.build()
				))
				.module("Tenant")
				.stack(List.of("Tenant"))
				.build();

		tenant.setMultiTenant(parentEntityDefinition.getMultiTenant());
		tenant.setIsGenerated(parentEntityDefinition.getIsGenerated());

		return groupMainGenerator.generateStreaming(
				EntityDefinitionDTO.builder()
						.outputDir(baseDir)
						.definition(tenant)
						.build()
		).then();
	}

	public Mono<Void> generate(EntityDefinition definition, String fullDir) {
		if (!definition.getMultiTenant()) {
			log.warn(" Tenant generation is only available for multi-tenant entities");
			return Mono.empty();
		}

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getTenantPackage();

		return createTenantEntity(baseDir, definition)
				.then();
	}
}
