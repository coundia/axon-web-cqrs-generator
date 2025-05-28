package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.*;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AndroidComposeGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public Flux<String> generate(EntityDefinition definition, String outputDir) {

		String entity = definition.getName();

		List<SharedTemplate> templates = new ArrayList<>(List.of(
				new SharedTemplate(entity, "front/android/model.mustache", null, outputDir + "/model", null, ".kt"),
				new SharedTemplate(entity + "ListViewModel", "front/android/entityListViewModel.mustache", null, outputDir + "/viewmodel", null, ".kt"),
				new SharedTemplate(entity + "PagedResponse", "front/android/entityPagedResponse.mustache", null, outputDir + "/dto", null, ".kt"),
				new SharedTemplate(entity + "Status", "front/android/entityStatus.mustache", null, outputDir + "/dto", null, ".kt"),
				new SharedTemplate(entity + "PullService", "front/android/entityPullService.mustache", null, outputDir + "/service", null, ".kt"),
				new SharedTemplate(entity + "PushService", "front/android/entityPushService.mustache", null, outputDir + "/service", null, ".kt"),
				new SharedTemplate(entity + "FormScreen", "front/android/entityFormScreen.mustache", null, outputDir + "/ui", null, ".kt"),
				new SharedTemplate(entity + "ListScreen", "front/android/entityListScreen.mustache", null, outputDir + "/ui", null, ".kt"),
				new SharedTemplate(entity + "RowItem", "front/android/entityRowItem.mustache", null, outputDir + "/ui", null, ".kt"),
				new SharedTemplate(entity + "SyncScreen", "front/android/entitySyncScreen.mustache", null, outputDir + "/ui", null, ".kt"),
				new SharedTemplate(entity + "ApiService", "front/android/apiEntity.mustache", null, outputDir + "/service", null, ".kt")
		));

		return Flux.fromIterable(templates)
				.doOnNext(t -> generateFile(t, definition))
				.map(SharedTemplate::getClassName);
	}

	private void generateFile(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		context.put("entity", definition.getName());
		context.put("shared", definition.getShared());
		context.put("title", definition.getTitle());

		context.put("header", !definition.getHeader().isEmpty());
		context.put("headerLowerCase", Utils.unCapitalize(definition.getHeader()));
		context.put("headerUpperCase", Utils.capitalize(definition.getHeader()));

		context.put("hasType", definition.getHasType());
		context.put("hasSummary", definition.getHasSummary());
		context.put("hasDate", definition.getHasDate());
		context.put("hasCategory", definition.getHasCategory());
		context.put("transactional", definition.getTransactional());
		context.put("isPublic", definition.getIsPublic());
		context.put("isAutoSave", definition.getIsAutoSave());
		context.put("isPremium", definition.getIsPremium());
		context.put("isChat", definition.getIsChat());

		context.put("entityLowerCase", Utils.unCapitalize(definition.getName()));

		context.put("fieldsDisplayed", definition.getFieldsToDisplay());
		context.put("fieldsAmount", definition.getFieldsAmount());

		context.put("editableFields", AndroidComposeFieldTransformer.transform(definition.getEditableFields(), definition.getName()));
		context.put("fields", AndroidComposeFieldTransformer.transform(definition.getFields(), definition.getName()));
		context.put("hasFiles", definition.getHasFiles());

		String content = templateEngine.render(template.getTemplatePath(), context);

		fileWriterService.write(template.getOutput(), template.getClassName() + template.getExt(), content);
	}
}
