package com.groupe2cs.generator.application.service.infrastructureservice;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public SecurityGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
		this.templateEngine = templateEngine;
		this.fileWriterService = fileWriterService;
		this.generatorProperties = generatorProperties;
	}

	public void generate(EntityDefinition definition, String fullDir) {

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getSecurityPackage();

		List<SharedTemplate>
				templates =
				List.of(new SharedTemplate("User",
								"infrastructure/security/userEntity.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getEntityPackage()),
						new SharedTemplate("Role",
								"infrastructure/security/roleEntity.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getEntityPackage()),
						new SharedTemplate("Permission",
								"infrastructure/security/permissionEntity.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getEntityPackage()),
						new SharedTemplate("UserRepository",
								"infrastructure/security/userRepository.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getRepositoryPackage()),
						new SharedTemplate("RoleRepository",
								"infrastructure/security/roleRepository.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getRepositoryPackage()),
						new SharedTemplate("PermissionRepository",
								"infrastructure/security/permissionRepository.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*"),
								baseDir + "/" + generatorProperties.getRepositoryPackage()),
						new SharedTemplate("JwtAuthenticationFilter",
								"infrastructure/security/jwtAuthenticationFilter.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) + ".*")

								,
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
								Set.of(
										Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
										Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
										Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) +
												".*"),
								baseDir + "/" + generatorProperties.getServicePackage()),

						new SharedTemplate("RegisterController",
								"infrastructure/security/registerController.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
										Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
												".*"),
								baseDir + "/" + generatorProperties.getControllerPackage()),

						new SharedTemplate("AuthController",
								"infrastructure/security/authController.mustache",
								Set.of(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
										Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
												".*"),
								baseDir + "/" + generatorProperties.getControllerPackage())
						);

		templates.forEach(template -> generateFile(template, definition));


		//Generate the Tests

		 List.of(
				new SharedTemplate("AuthControllerTests",
						"infrastructure/security/authControllerTests.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
										".*"),
						fullDir
				),
				 new SharedTemplate("RegisterControllerTests",
						 "infrastructure/security/registerControllerTests.mustache",
						 Set.of(
								 Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								 Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
								 Utils.getPackage(baseDir + "/" + generatorProperties.getConfigPackage()) + ".*",
								 Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*",
								 Utils.getPackage(baseDir + "/" + generatorProperties.getServicePackage()) +
										 ".*"),
						 fullDir
				 )
		).forEach(t -> generateTests(t, definition));

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