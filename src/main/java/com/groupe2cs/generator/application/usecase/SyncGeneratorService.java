package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SyncGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public void generate(EntityDefinition definition, String baseDir) {
		String outputDto = baseDir + "/" + generatorProperties.getDtoPackage();
		String useCaseDir = baseDir + "/" + generatorProperties.getApplicationUseCasePackage() ;
		String outputController = baseDir + "/" + generatorProperties.getControllerPackage();

		String sharedDir = Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage();
		String testDir = Utils.getTestDir(baseDir) + "/" + generatorProperties.getControllerPackage();

		List<SharedTemplate> syncTemplates = List.of(
				new SharedTemplate(
						definition.getName() + "SyncRequest",
						"application/dtoSyncRequest.mustache",
						Set.of(),
						outputDto
				),
				new SharedTemplate(
						definition.getName() + "DeltaDto",
						"application/dtoEntityDelta.mustache",
						Set.of(),
						outputDto
				),
				new SharedTemplate(
						definition.getName() + "SyncApplicationService",
						"application/syncApplicationService.mustache",
						Set.of(
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
								Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*"
						),
						useCaseDir
				),
				new SharedTemplate(
						 definition.getName() + "SyncController",
						"presentation/syncController.mustache",
						Set.of(
								Utils.getPackage(sharedDir + "/" + generatorProperties.getInfrastructurePackage()) + ".audit.RequestContext",
								Utils.getPackage(baseDir + "/" + generatorProperties.getApplicationUseCasePackage()) + ".*",
								Utils.getPackage(sharedDir + "/" + generatorProperties.getApplicationPackage()) + ".*",
								Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*"
						),
						outputController
				),
				new SharedTemplate(
						definition.getName() + "SyncControllerTests",
						"tests/syncControllerTests.mustache",
						Set.of(
								Utils.getTestPackage(Utils.getParent(sharedDir) + "/security/" + generatorProperties.getControllerPackage()) + ".UserFixtures",
								//Utils.getTestPackage(Utils.getParent(sharedDir) + "/tenant/" + generatorProperties.getControllerPackage()) + ".TenantFixtures",
								Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".*",
								Utils.getPackage(sharedDir + "/" + generatorProperties.getApplicationPackage()) + ".*",
								Utils.getTestPackage(Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage()) + ".*",
								Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*"
						),
						testDir,
						Utils.getTestPackage(outputController)
				)
		);

		syncTemplates.forEach(template -> generateSyncFile(template, definition));
	}

	private void generateSyncFile(SharedTemplate template, EntityDefinition definition) {

		 String packageName = template.getPackageName();

		 if(packageName == null || packageName.isEmpty() || packageName.equals("null")) {
			 packageName = Utils.getPackage(template.getOutput());
		 }

		Map<String, Object> context = new HashMap<>();

		context.put("apiPrefix", definition.getApiPrefix());
		context.put("package", packageName);
		context.put("imports", template.getImports());
		context.put("name", Utils.capitalize(definition.getName()));
		context.put("entity", Utils.capitalize(definition.getName()));
		context.put("className", template.getClassName());
		context.put("nameLowercase", definition.getName().toLowerCase());
		context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
		context.put("allFields", FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName()));
		context.put("hasFiles", !definition.getFieldFiles().isEmpty());
		context.put("fieldFiles", FieldTransformer.transform(definition.getFieldFiles(), definition.getName()));
		context.put("editableFields", FieldTransformer.transform(definition.getEditableFields(), definition.getName()));
		context.put("isMultiTenant", definition.getMultiTenant());

		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(template.getOutput(), template.getClassName() + ".java", content);
	}
}
