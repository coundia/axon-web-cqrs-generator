package com.groupe2cs.generator.application.service.domainservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public VoGeneratorService(
			TemplateEngine templateEngine,
			FileWriterService fileWriterService,
			GeneratorProperties generatorProperties
	) {
		this.templateEngine = templateEngine;
		this.fileWriterService = fileWriterService;
		this.generatorProperties = generatorProperties;
	}

	public void generate(EntityDefinition definition, String baseDir) {
		String outputDir = baseDir + "/" + this.generatorProperties.getVoPackage();
		Set<String> imports = new LinkedHashSet<>();

		for (FieldDefinition field : definition.getAllFieldsWithoutOneToMany()) {
			String voName = definition.getName() + Utils.capitalize(field.getName());
			String exceptionName = voName + "NotValid";

			Map<String, Object> context = new HashMap<>(definition.toMap());

			context.put("package", Utils.getPackage(outputDir));
			context.put("voName", voName);
			context.put("type", field.getPrimitiveType());
			context.put("name", field.getName());
			context.put("nullable", field.getNullable());
			context.put("equalsExpression", "this." + field.getName() + ".equals(that." + field.getName() + ")");
			context.put("hashCodeExpression", "java.util.Objects.hash(" + field.getName() + ")");
			context.put("exceptionName", exceptionName);
			context.put("exceptionMessage", Utils.capitalize(field.getName()) + " is invalid");

			if (field.getPrimitiveType().equalsIgnoreCase("String")) {
				context.put("invalidCondition", field.getName() + " == null || " + field.getName() + ".isBlank()");
			} else if (field.getPrimitiveType().equalsIgnoreCase("int") ||
					field.getPrimitiveType().equalsIgnoreCase("Integer")) {
				context.put("invalidCondition", field.getName() + " < 0");
			} else {
				context.put("invalidCondition", field.getName() + " == null");
			}

			imports.add("java.util.Objects");
			imports.add(Utils.getPackage(outputDir).replace(".valueObject", ".exception") + "." + exceptionName);

			context.put("imports", imports);

			String voCode = templateEngine.render("domain/vo.mustache", context);
			fileWriterService.write(outputDir, voName + ".java", voCode);

			Map<String, Object> exContext = new HashMap<>();
			exContext.put("package", Utils.getPackage(outputDir).replace(".valueObject", ".exception"));
			exContext.put("exceptionName", exceptionName);

			String exceptionCode = templateEngine.render("domain/voException.mustache", exContext);
			String exOutput = outputDir.replace("valueObject", "exception");
			fileWriterService.write(exOutput, exceptionName + ".java", exceptionCode);
		}
	}
}
