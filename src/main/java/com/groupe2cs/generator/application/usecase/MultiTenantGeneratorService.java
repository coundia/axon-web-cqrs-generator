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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class MultiTenantGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;
	private final GroupMainGenerator groupMainGenerator;

	private Mono<Void> createTenantEntity(String baseDir, EntityDefinition parentEntityDefinition) {
		EntityDefinition tenant = EntityDefinition.builder()
				.name("Tenant")
				.table("tenants")
				.apiPrefix("/admin")
				.fields(List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("name").unique(true).type("String").build(),

						FieldDefinition.builder().name("description").type("String").columnDefinition("TEXT").nullable(true).build(),
						FieldDefinition.builder().name("domain").type("String").nullable(true).build(),
						FieldDefinition.builder().name("language").type("String").nullable(true).build(),
						FieldDefinition.builder().name("active").type("Boolean").nullable(true).build()
				))
				.module("Tenant")
				.stack(List.of("Tenant"))
				.build();

			tenant.setMultiTenant(parentEntityDefinition.getMultiTenant());
			tenant.setIsGenerated(parentEntityDefinition.getIsGenerated());
			tenant.setMultiTenant(parentEntityDefinition.getMultiTenant());

		return groupMainGenerator.generateStreaming(
				EntityDefinitionDTO.builder()
						.outputDir(baseDir)
						.definition(tenant)
						.build()
		).then();
	}

	public Flux<String> generate(EntityDefinition definition, String fullDir) {
		if (!definition.getMultiTenant()) {
			return Flux.just("❌ MultiTenant not enabled");
		}

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getTenantPackage();

		List<SharedTemplate> templates = List.of(
				SharedTemplate.builder()
						.output(baseDir+"/"+generatorProperties.getInfrastructurePackage())
						.className("CurrentTenantIdentifierResolverImpl")
						.templatePath("infrastructure/tenant/currentTenantIdentifierResolverImpl.mustache")
						.imports(
								Set.of(
								Utils.getPackage(baseDir+"/"+generatorProperties.getRepositoryPackage())+".TenantRepository",
								Utils.getPackage(baseDir+"/"+generatorProperties.getEntityPackage())+".Tenant"
								)
						).build()
		);

		//test
		List<SharedTemplate> templateTests = List.of(
				SharedTemplate.builder()
						.output(baseDir+"/"+generatorProperties.getInfrastructurePackage())
						.className("CurrentTenantIdentifierResolverTest")
						.templatePath("infrastructure/tenant/currentTenantIdentifierResolverTest.mustache")
						.imports(
								Set.of(
										Utils.getPackage(baseDir+"/"+generatorProperties.getRepositoryPackage())+".TenantRepository",
										Utils.getPackage(baseDir+"/"+generatorProperties.getEntityPackage())+".Tenant",
										Utils.getPackage(baseDir+"/"+generatorProperties.getInfrastructurePackage())+".CurrentTenantIdentifierResolverImpl"
								)
						).build()
		);

		Flux<String> templateFlux = Flux.fromIterable(templates)
				.map(t -> {
					Map<String, Object> context = new HashMap<>();
					String outputDir = t.getOutput();
					context.put("package", Utils.getPackage(outputDir));
					context.put("imports", t.getImports());
					context.put("className", t.getClassName());
					String content = templateEngine.render(t.getTemplatePath(), context);
					fileWriterService.write(outputDir, t.getClassName() + ".java", content);
					return "✔️ Generated: " + t.getClassName();
				});

		Flux<String> templateTestFlux = Flux.fromIterable(templateTests)
				.map(t -> {

					Map<String, Object> context = new HashMap<>();

					String rootDirTest = t.getOutput();
					context.put("package", Utils.getTestPackage(rootDirTest));
					context.put("imports", t.getImports());
					context.put("className", t.getClassName());

					String content = templateEngine.render(t.getTemplatePath(), context);
					fileWriterService.write(Utils.getTestDir(rootDirTest), t.getClassName() + ".java", content);
					return "✔️ Generated: " + t.getClassName();
				});

		return createTenantEntity(baseDir,definition)
				.thenMany(templateFlux)
				.concatWith(templateTestFlux);
	}
}
