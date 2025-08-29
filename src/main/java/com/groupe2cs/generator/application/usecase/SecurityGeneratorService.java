package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
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

import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class SecurityGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;
	private final GroupMainGenerator groupMainGenerator;

	private Mono<Void> createEntityDefinitionForSecurity(String baseDir, EntityDefinition parentEntityDefinition) {


		EntityDefinition refreshToken = new EntityDefinition("RefreshToken", List.of(
				FieldDefinition.builder().name("id").type("String").build(),
				FieldDefinition.builder().name("token").columnDefinition("TEXT").
						type("String").build(),
				FieldDefinition.builder().name("username").type("String").build(),
				FieldDefinition.builder().name("expiration").type("java.time.Instant").build()
		), "refresh_tokens");

		refreshToken.getSkip().add("presentation");
		refreshToken.setAuditable(true);

		List<EntityDefinition> entities = List.of(
				EntityDefinition.builder()
						.name("User")
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("username").type("String").build(),
								FieldDefinition.builder().name("password").type("String").build(),
								FieldDefinition.builder().name("firstName").type("String").nullable(true)
										.defaultValue("").build(),
								FieldDefinition.builder().name("lastName").type("String").nullable(true).build(),
								FieldDefinition.builder().name("email").type("String").nullable(true).build(),
								FieldDefinition.builder().name("telephone").type("String").nullable(true).build(),
								FieldDefinition.builder().name("limitPerDay")
										.columnDefinition("INT DEFAULT 10")
										.type("Integer").nullable(true).defaultValue("10").build(),

								FieldDefinition.builder().name("isPremium").type("Boolean").nullable(true).build(),
								FieldDefinition.builder().name("enabled").type("Boolean").nullable(true).build(),
								FieldDefinition.builder().name("isBan").type("Boolean").nullable(true).build(),

								FieldDefinition.builder().name("message")
										.type("String")
										.columnDefinition("Text")
										.nullable(true).build(),

								FieldDefinition.builder().name("userRoles").type("Set<UserRole>").relation("OneToMany")
										.build()
						))
						.auditable(true)
						//.entity("User")
						.table("users")
						.build(),

				EntityDefinition.builder()
						.name("PasswordReset")
						.auditable(true)
						.skip(
								List.of("presentation")
						)
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("token").columnDefinition("TEXT").type("String").build(),
								FieldDefinition.builder().name("username").type("String").build(),
								FieldDefinition.builder().name("expiration").type("java.time.Instant").build()
						))
						.table("password_resets")
						.build(),

				EntityDefinition.builder()
						.name("ApiKey")
						.auditable(true)
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("appKey").columnDefinition("TEXT").type("String")
										.build(),
								FieldDefinition.builder().name("username").type("String").build(),
								FieldDefinition.builder().name("active").type("Boolean").build(),
								FieldDefinition.builder().name("createdAt").type("java.time.Instant").build(),
								FieldDefinition.builder().name("expiration").type("java.time.Instant").build()
						))
						.table("api_keys")
						.build(),

				refreshToken,

				EntityDefinition.builder()
						.name("Role")
						.table("roles")
						.auditable(true)
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("name").type("String").build(),
								FieldDefinition.builder().name("rolePermissions").type("Set<RolePermission>")
										.relation("OneToMany").build()
						))
						.build(),

				EntityDefinition.builder()
						.name("Permission")
						.auditable(true)
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("name").type("String").build()
						))
						.table("permissions")
						.build(),

				EntityDefinition.builder()
						.name("UserRole")
						.auditable(true)
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("user").type("User").relation("ManyToOne").build(),
								FieldDefinition.builder().name("role").type("Role").relation("ManyToOne").build()
						))
						.table("user_roles")
						.build(),

				EntityDefinition.builder()
						.auditable(true)
						.name("RolePermission")
						.apiPrefix("/admin")
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("role").type("Role").relation("ManyToOne").build(),
								FieldDefinition.builder().name("permission").type("Permission").relation("ManyToOne")
										.build()
						))
						.table("role_permissions")
						.build(),

				EntityDefinition.builder()
						.name("VerificationCode")
						.auditable(true)
						.fields(List.of(
								FieldDefinition.builder().name("id").type("String").build(),
								FieldDefinition.builder().name("notes").columnDefinition("TEXT").nullable(true)
										.type("String").build(),
								FieldDefinition.builder().name("token").columnDefinition("TEXT").nullable(true)
										.type("String").build(),
								FieldDefinition.builder().name("username").type("String").nullable(true).build(),
								FieldDefinition.builder().name("phone").type("String").nullable(true).build(),
								FieldDefinition.builder().name("email").type("String").nullable(true).build(),
								FieldDefinition.builder().name("code").type("String").nullable(true).build(),
								FieldDefinition.builder().name("status").type("String").nullable(true).build(),
								FieldDefinition.builder().name("source").type("String").nullable(true).build(),
								FieldDefinition.builder().name("expiration").nullable(true).type("java.time.Instant")
										.build()
						))
						.table("verification_codes")
						.build()
		);


		return Flux.fromIterable(entities)
				.flatMap(entity -> {
					entity.setModule("Security");
					entity.setStack(List.of("Security"));
					entity.setIsGenerated(true);
					entity.setMultiTenant(parentEntityDefinition.getMultiTenant());
					return groupMainGenerator.generateStreaming(
							EntityDefinitionDTO.builder()
									.outputDir(baseDir)
									.definition(entity)
									.build()
					);
				})
				.then()
				.doOnError(error ->
						log.error("❌Erreur lors de la génération des entités de sécurité", error)
				);

	}

	public Flux<String> generate(EntityDefinition definition, String fullDir) {

		if (!definition.isInStack("Security")) {
			return Flux.just("❌ Security not in stack");
		}

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getSecurityPackage();

		String shareDir = Utils.getParent(fullDir) + "/" + generatorProperties.getSharedPackage();

		List<String> messages = new ArrayList<>();

		Set<String> importInit = new HashSet<>(
				Set.of(
						"static " +
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
								".ConstanteConfig.*"

				)
		);

		String rootDir = Utils.getParent(fullDir);

		importInit.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");
		importInit.add(Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*");

		Set<String> importsRegisterUser = new HashSet<>(
				Set.of(
						Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
						Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
						Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
						Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*"
				)
		);

		if (definition.getMultiTenant()) {
			importInit.add(Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant");
			importsRegisterUser.add(
					Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant");
			importInit.add(
					Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getRepositoryPackage()) +
							".TenantRepository"
			);
		}


		List<SharedTemplate> templates = List.of(

				new SharedTemplate("SecurityConfig",
						"infrastructure/security/securityConfig.mustache",
						Set.of(
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.API_KEY_HEADER",
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.TENANT_HEADER",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
										".CustomUserDetailsService"
						),
						baseDir + "/" + generatorProperties.getConfigPackage()),

				new SharedTemplate("WebConfig",
						"infrastructure/security/webConfig.mustache",
						null
						,
						baseDir + "/" + generatorProperties.getConfigPackage()),

				new SharedTemplate("ConstanteConfig",
						"infrastructure/security/constanteConfig.mustache",
						null,
						baseDir + "/" + generatorProperties.getConfigPackage()),

				new SharedTemplate("CurrentTenantIdentifierResolverImpl",
						"infrastructure/security/currentTenantIdentifierResolverImpl.mustache",
						Set.of(
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.*",
								Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getRepositoryPackage()) +
										".TenantRepository"
						),
						baseDir + "/" + generatorProperties.getConfigPackage()),

				new SharedTemplate("SecurityInitializer",
						"infrastructure/security/securityInitializer.mustache",
						importInit,
						baseDir + "/" + generatorProperties.getConfigPackage()),


				new SharedTemplate("ApiKeyFilter",
						"infrastructure/security/apiKeyFilter.mustache",
						Set.of(
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.API_KEY_HEADER",
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.TENANT_HEADER",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) +
										".ApiKeyResponse",
								Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".ApiKeyAppKey",
								Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) +
										".FindByApiKeyAppKeyQuery"
						),
						baseDir + "/" + generatorProperties.getConfigPackage()),


				new SharedTemplate("UserPrincipal",
						"infrastructure/security/userPrincipal.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/infrastructure/audit") + ".IdentifiableUser",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getServicePackage()
				),
				new SharedTemplate("JwtService",
						"infrastructure/security/jwtService.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.*"
						),
						baseDir + "/" + generatorProperties.getServicePackage()),

				new SharedTemplate("CustomUserDetailsService",
						"infrastructure/security/userDetailsService.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) +
										".User",
								Utils.getPackage(Utils.getParent(baseDir) +
										"/" +
										generatorProperties.getTenantPackage() +
										"/" +
										generatorProperties.getEntityPackage()) + ".Tenant"
						),
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
						importsRegisterUser
						,
						baseDir + "/" + generatorProperties.getServicePackage()),
				new SharedTemplate("RegisterController",
						"infrastructure/security/registerController.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						baseDir + "/" + generatorProperties.getControllerPackage()),


				new SharedTemplate("AuthController",
						"infrastructure/security/authController.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getControllerPackage()),

				new SharedTemplate("AuthService",
						"infrastructure/security/authService.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest"
						),
						baseDir + "/" + generatorProperties.getServicePackage()),

				new SharedTemplate("AuthMe",
						"infrastructure/security/authMe.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getInfrastructurePackage()) +
										".audit.RequestContext",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getApplicationPackage()) +
										".ApiResponseDto",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
										".UserPrincipal"
						),
						baseDir + "/" + generatorProperties.getControllerPackage()),

				new SharedTemplate("ForgotPasswordRequestDto",
						"infrastructure/security/forgotPasswordRequestDto.mustache",
						Set.of(
						),
						baseDir + "/" + generatorProperties.getDtoPackage()),

				new SharedTemplate("ResetPasswordDto",
						"infrastructure/security/dtoResetPassword.mustache",
						Set.of(
						),
						baseDir + "/" + generatorProperties.getDtoPackage()),

				new SharedTemplate("RefreshTokenDto",
						"infrastructure/security/dtoRefreshToken.mustache",
						null,
						baseDir + "/" + generatorProperties.getDtoPackage()),

				new SharedTemplate("PasswordResetService",
						"infrastructure/security/passwordResetService.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDomainPackage()) +
										".MailSender",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEventPackage()) +
										".PasswordResetCreatedEvent",
								Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getServicePackage()),

				new SharedTemplate("RefreshTokenService",
						"infrastructure/security/refreshTokenService.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) +
										".RefreshTokenRepository",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) +
										".RefreshToken",
								Utils.getPackage(Utils.getParent(shareDir) +
										"/tenant/" +
										generatorProperties.getEntityPackage()) + ".Tenant",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".User",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getInfrastructurePackage()) +
										".audit.RequestContext"
						),
						baseDir + "/" + generatorProperties.getServicePackage()),

				new SharedTemplate("ForgotPasswordController",
						"infrastructure/security/forgotPasswordController.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getControllerPackage()),

				new SharedTemplate("CustomFindByApiKeyAppKeyHandler",
						"infrastructure/security/customFindByApiKeyAppKeyHandler.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getInfrastructurePackage()) +
										".audit.RequestContext",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getApplicationPackage()) +
										".ApiResponseDto",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) +
										".ApiKeyResponse",
								Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) +
										".ApiKeyMapper",
								Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) +
										".FindByApiKeyAppKeyQuery",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".ApiKey",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) +
										".ApiKeyRepository"
						),
						baseDir + "/" + generatorProperties.getQueryHandlerPackage()),

				new SharedTemplate("RefreshTokenController",
						"infrastructure/security/refreshTokenController.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getInfrastructurePackage()) +
										".audit.RequestContext",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getApplicationPackage()) +
										".ApiResponseDto",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getControllerPackage()),

				new SharedTemplate("LogoutController",
						"infrastructure/security/logoutController.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getInfrastructurePackage()) +
										".audit.RequestContext",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir + "/" + generatorProperties.getApplicationPackage()) +
										".ApiResponseDto",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) +
										".RefreshTokenRepository",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"
						),
						baseDir + "/" + generatorProperties.getControllerPackage())


		);

		templates.forEach(template -> {
			generateFile(template, definition);
			messages.add("✔️ Generated: " + template.getClassName());
		});


		List<SharedTemplate> tests = List.of(
				new SharedTemplate("AuthControllerTests",
						"infrastructure/security/authControllerTests.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						fullDir),

				new SharedTemplate("RegisterUserTests",
						"infrastructure/security/registerUserTests.mustache",
						Set.of(
								"com.fasterxml.jackson.databind.ObjectMapper",
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.API_KEY_HEADER",
								"static " +
										Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) +
										".ConstanteConfig.TENANT_HEADER",
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getRepositoryPackage()) +
										".TenantRepository",
								Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getEntityPackage()) +
										".Tenant",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"
						),
						fullDir),

				new SharedTemplate("RegisterControllerTests",
						"infrastructure/security/registerControllerTests.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*"),
						fullDir),

				new SharedTemplate("RefreshTokenControllerTest",
						"infrastructure/security/refreshTokenControllerTest.mustache",
						Set.of(

								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(shareDir) + ".BaseIntegrationTests"
						),
						fullDir),

				new SharedTemplate("AuthMeControllerIntegrationTest",
						"infrastructure/security/authMeControllerIntegrationTest.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(shareDir) + ".BaseIntegrationTests"
						),
						fullDir),

				new SharedTemplate("ForgotPasswordControllerTest",
						"infrastructure/security/forgotPasswordControllerTest.mustache",
						Set.of(
								Utils.getPackage(shareDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
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

		return createEntityDefinitionForSecurity(baseDir, definition).thenMany(Flux.fromIterable(messages));
	}

	private void generateFile(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		String outputDir = template.getOutput();

		context.put("apiPrefix", definition.getApiPrefix());

		context.put("package", Utils.getPackage(outputDir));
		context.put("packageTest", Utils.getTestPackage(Utils.getPackage(outputDir)));
		context.put("imports", template.getImports());
		context.put("entity", Utils.capitalize(definition.getName()));
		context.put("entityLowerCase", Utils.lowerCase(definition.getName()));
		context.put("className", template.getClassName());
		context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
		context.put("isMultiTenant", definition.getMultiTenant());
		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(outputDir, template.getClassName() + ".java", content);
	}

	private void generateTests(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		String baseDir = template.getOutput();
		baseDir = Utils.getParent(baseDir);
		String fullPath = baseDir + "/" + generatorProperties.getSecurityPackage();
		String packageName = Utils.getTestPackage(fullPath);

		Set<String> allImports = new HashSet<>(template.getImports());

		allImports.add(
				"static " +
						Utils.getPackage(fullPath + "/" + generatorProperties.getConfigPackage()) +
						".ConstanteConfig.*"
		);

		context.put("package", packageName);
		context.put("imports", allImports);
		context.put("className", template.getClassName());
		context.put("isMultiTenant", definition.getMultiTenant());
		String outputDir = Utils.getTestDir(fullPath);
		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(outputDir, template.getClassName() + ".java", content);
	}
}
