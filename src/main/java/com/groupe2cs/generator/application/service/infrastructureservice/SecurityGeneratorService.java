package com.groupe2cs.generator.application.service.infrastructureservice;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
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

import java.util.*;

@RequiredArgsConstructor
@Service
public class SecurityGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;
	private final GroupMainGenerator groupMainGenerator;

	private Mono<Void> createEntityDefinitionForSecurity(String baseDir) {

		String entityPackage = Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage());

		List<EntityDefinition> entities = List.of(
				new EntityDefinition("User", List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("username").type("String").unique(true).build(),
						FieldDefinition.builder().name("password").type("String").build(),
						FieldDefinition.builder().name("email").type("String").unique(true).nullable(true).build(),
						FieldDefinition.builder().name("userRoles").type("List<UserRole>").relation("OneToMany").build()
				), "users"),

				new EntityDefinition("Role", List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("name").type("String").unique(true).build(),
						FieldDefinition.builder().name("rolePermissions").type("List<RolePermission>").relation("OneToMany").build()
				), "roles"),

				new EntityDefinition("Permission", List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("name").type("String").unique(true).build()
				), "permissions"),

				new EntityDefinition("UserRole", List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("user").type("User").relation("ManyToOne").build(),
						FieldDefinition.builder().name("role").type("Role").relation("ManyToOne").build()
				), "user_roles"),

				new EntityDefinition("RolePermission", List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("role").type("Role").relation("ManyToOne").build(),
						FieldDefinition.builder().name("permission").type("Permission").relation("ManyToOne").build()
				), "role_permissions")
		);

		return Flux.fromIterable(entities)
				.flatMap(entity -> {
					entity.setModule("Security");
					return groupMainGenerator.generateStreaming(
							EntityDefinitionDTO.builder()
									.outputDir(baseDir)
									.definition(entity)
									.build()
					);
				})
				.then();

	}

	public Flux<String> generate(EntityDefinition definition, String fullDir) {

		if(!definition.isInStack("Security")) {
			return Flux.just("❌ Security not in stack");
		}

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getSecurityPackage();
		List<String> messages = new ArrayList<>();

		List<SharedTemplate> templates = List.of(
				new SharedTemplate("JwtAuthenticationFilter",
						"infrastructure/security/jwtAuthenticationFilter.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						baseDir + "/" + generatorProperties.getConfigPackage()),
				new SharedTemplate("SecurityConfig",
						"infrastructure/security/securityConfig.mustache",
						null,
						baseDir + "/" + generatorProperties.getConfigPackage()),
				new SharedTemplate("JwtProperties",
						"infrastructure/security/jwtProperties.mustache",
						null,
						baseDir + "/" + generatorProperties.getConfigPackage()),
				new SharedTemplate("UserPrincipal",
						"infrastructure/security/userPrincipal.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
						baseDir + "/" + generatorProperties.getServicePackage()),
				new SharedTemplate("JwtService",
						"infrastructure/security/jwtService.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*"),
						baseDir + "/" + generatorProperties.getServicePackage()),
				new SharedTemplate("CustomUserDetailsService",
						"infrastructure/security/userDetailsService.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*"),
						baseDir + "/" + generatorProperties.getServicePackage()),
				new SharedTemplate("AuthRequestDto",
						"infrastructure/security/authRequestDto.mustache",
						null,
						baseDir + "/" + generatorProperties.getDtoPackage()),
				new SharedTemplate("AuthResponseDto",
						"infrastructure/security/authResponseDto.mustache",
						null,
						baseDir + "/" + generatorProperties.getDtoPackage()),
				new SharedTemplate("SecurityExceptionHandler",
						"infrastructure/security/securityExceptionHandler.mustache",
						null,
						baseDir + "/" + generatorProperties.getControllerPackage()),
				new SharedTemplate("RegisterUser",
						"infrastructure/security/registerUser.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*"),
						baseDir + "/" + generatorProperties.getServicePackage()),
				new SharedTemplate("RegisterController",
						"infrastructure/security/registerController.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						baseDir + "/" + generatorProperties.getControllerPackage()),
				new SharedTemplate("AuthController",
						"infrastructure/security/authController.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						baseDir + "/" + generatorProperties.getControllerPackage())
		);

		templates.forEach(template -> {
			generateFile(template, definition);
			messages.add("✔️ Generated: " + template.getClassName());
		});

		List<SharedTemplate> tests = List.of(
				new SharedTemplate("AuthControllerTests",
						"infrastructure/security/authControllerTests.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						fullDir),
				new SharedTemplate("RegisterControllerTests",
						"infrastructure/security/registerControllerTests.mustache",
						Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						fullDir)
		);

		tests.forEach(t -> {
			generateTests(t, definition);
			messages.add("🧪 Test generated: " + t.getClassName());
		});

		return createEntityDefinitionForSecurity(baseDir).thenMany(Flux.fromIterable(messages));
	}

	private void generateFile(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		String outputDir = template.getOutput();
		context.put("package", Utils.getPackage(outputDir));
		context.put("packageTest", Utils.getTestPackage(Utils.getPackage(outputDir)));
		context.put("imports", template.getImports());
		context.put("entity", Utils.capitalize(definition.getName()));
		context.put("entityLowerCase", Utils.lowerCase(definition.getName()));
		context.put("className", template.getClassName());
		context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(outputDir, template.getClassName() + ".java", content);
	}

	private void generateTests(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		String baseDir = template.getOutput();
		baseDir = Utils.getParent(baseDir);
		String fullPath = baseDir + "/" + generatorProperties.getSecurityPackage();
		String packageName = Utils.getTestPackage(fullPath);
		context.put("package", packageName);
		context.put("imports", template.getImports());
		String outputDir = Utils.getTestDir(fullPath);
		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(outputDir, template.getClassName() + ".java", content);
	}
}
