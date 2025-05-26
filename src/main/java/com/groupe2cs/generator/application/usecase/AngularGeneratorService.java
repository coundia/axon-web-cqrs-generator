package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.AngularFieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.SwiftFieldTransformer;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AngularGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;

	public void generate(EntityDefinition definition, String outputDir) {
		String entity = Utils.unCapitalize(definition.getName());

		String assets = Utils.getSrcDir(outputDir) + "/assets";

		List<SharedTemplate> templates = List.of(
				new SharedTemplate(entity + ".routes",
						"front/angular/routes.mustache",
						null,
						outputDir,
						null,
						".ts"),
				new SharedTemplate(entity + "-form.component",
						"front/angular/formComponent.mustache",
						null,
						outputDir + "/components",
						null,
						".ts"),
				new SharedTemplate(entity + "-form.component",
						"front/angular/formHtml.mustache",
						null,
						outputDir + "/components",
						null,
						".html"),
				new SharedTemplate(entity + "-list.component",
						"front/angular/listComponent.mustache",
						null,
						outputDir + "/components",
						null,
						".ts"),
				new SharedTemplate(entity + "-list.component",
						"front/angular/listHtml.mustache",
						null,
						outputDir + "/components",
						null,
						".html"),
				new SharedTemplate(entity + ".model",
						"front/angular/model.mustache",
						null,
						outputDir + "/models",
						null,
						".ts"),
				new SharedTemplate(entity + ".service",
						"front/angular/service.mustache",
						null,
						outputDir + "/services",
						null,
						".ts"),

				new SharedTemplate(
						Utils.unCapitalize(entity),
						"front/angular/translation.fr.mustache",
						null,
						assets + "/i18n/fr",
						null,
						".json")
		);

		templates.forEach(t -> generateFile(t, definition));
	}

	private void generateFile(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		context.put("entity", definition.getName());
		context.put("entityLowerCase", Utils.unCapitalize(definition.getName()) );

		context.put("fields", AngularFieldTransformer.transform(definition.getFields(), definition.getName()));
		context.put("allFields", AngularFieldTransformer.transform(definition.getAllFields(), definition.getName()));
		context.put("fieldsToDisplay", AngularFieldTransformer.transform(definition.getFieldWithDisplayName(), definition.getName()));

		context.put("open", "{{");
		context.put("close", "}}");


		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(template.getOutput(), template.getClassName() + template.getExt(), content);
	}
}
