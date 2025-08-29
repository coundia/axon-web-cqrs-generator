package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class FindByFieldQueryGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public FindByFieldQueryGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
		this.templateEngine = templateEngine;
		this.fileWriterService = fileWriterService;
		this.generatorProperties = generatorProperties;
	}

	public void generate(EntityDefinition definition, String baseDir) {
		String outputDir = baseDir + "/" + generatorProperties.getQueryPackage();
		String packageName = Utils.getPackage(outputDir);

		var fields = definition.searchFields();

		for (var field : fields) {
			field.setNameCapitalized(capitalize(field.getName()));

			String nameJpa = Utils.capitalize(field.getName());

			if ("manyToOne".equalsIgnoreCase(field.getRelation())) {
				nameJpa = nameJpa + "Id";
			}

			field.setNameJpa(nameJpa);

			Map<String, Object> context = new HashMap<>();
			context.put("package", packageName);
			context.put("field", field);
			context.put("name", definition.getName());

			String className = "FindBy" + definition.getName() + field.getNameCapitalized() + "Query";
			context.put("className", className);

			String sharedDir = Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage();

			Set<String> imports = new LinkedHashSet<>();
			imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
			imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".*");

			context.put("imports", imports);

			String content = templateEngine.render("application/findByFieldQuery.mustache", context);
			fileWriterService.write(outputDir, className + ".java", content);
		}
	}

	private String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
