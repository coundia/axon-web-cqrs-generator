package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class PagedResponseGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties properties;

	public PagedResponseGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties properties) {
		this.templateEngine = templateEngine;
		this.fileWriterService = fileWriterService;
		this.properties = properties;
	}

	public void generate(EntityDefinition definition, String baseDir) {
		Map<String, Object> context = new HashMap<>();
		String outputDir = baseDir + "/" + properties.getDtoPackage();
		context.put("package", Utils.getPackage(outputDir));
		context.put("name", definition.getName());
		context.put("entity", definition.getEntity());

		Set<String> imports = new LinkedHashSet<>();
		imports.add(Utils.getPackage(baseDir + "/" + properties.getEntityPackage()) + ".*");
		context.put("imports", imports);

		String content = templateEngine.render("application/pagedResponse.mustache", context);
		fileWriterService.write(outputDir, definition.getName() + "PagedResponse.java", content);
	}
}
