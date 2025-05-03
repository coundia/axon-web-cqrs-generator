package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.SwiftFieldTransformer;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

@RequiredArgsConstructor
@Service
public class SwiftUiGeneratorService {

	private final TemplateEngine templateEngine;
	private final FileWriterService fileWriterService;
	private final GeneratorProperties generatorProperties;

	public Flux<String> generate(EntityDefinition definition, String outputDir) {

		String entity = definition.getName();

		List<SharedTemplate> templates = List.of(
				new SharedTemplate(entity, "front/swift/model.mustache", null, outputDir + "/Models", null, ".swift"),
				new SharedTemplate(entity + "DeltaDto",
						"front/swift/entityDeltaDto.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift"),
				new SharedTemplate(entity + "ListViewModel",
						"front/swift/entityListViewModel.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift"),
				new SharedTemplate(entity + "PagedResponse",
						"front/swift/entityPagedResponse.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift"),
				new SharedTemplate(entity + "Status",
						"front/swift/entityStatus.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift"),
				new SharedTemplate("Scroll" + entity + "ListViewModel",
						"front/swift/scrollEntityListViewModel.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift"),

				new SharedTemplate(entity + "SyncManager",
						"front/swift/entitySyncManager.mustache",
						null,
						outputDir + "/Services",
						null,
						".swift"),
				new SharedTemplate(entity + "SyncService",
						"front/swift/entitySyncService.mustache",
						null,
						outputDir + "/Services",
						null,
						".swift"),

				new SharedTemplate("Sync" + entity + "UseCase",
						"front/swift/syncEntityUseCase.mustache",
						null,
						outputDir + "/UseCases",
						null,
						".swift"),

				new SharedTemplate(entity + "FormView",
						"front/swift/entityFormView.mustache",
						null,
						outputDir + "/Views",
						null,
						".swift"),
				new SharedTemplate(entity + "ListView",
						"front/swift/entityListView.mustache",
						null,
						outputDir + "/Views",
						null,
						".swift"),
				new SharedTemplate(entity + "PickerView",
						"front/swift/entityPickerView.mustache",
						null,
						outputDir + "/Views",
						null,
						".swift"),
				new SharedTemplate(entity + "RowView",
						"front/swift/entityRowView.mustache",
						null,
						outputDir + "/Views",
						null,
						".swift"),
				new SharedTemplate(entity + "SyncView",
						"front/swift/entitySyncView.mustache",
						null,
						outputDir + "/Views",
						null,
						".swift")
				,
				new SharedTemplate("Api"+entity ,
						"front/swift/apiEntity.mustache",
						null,
						outputDir + "/Models",
						null,
						".swift")

		);

		return Flux.fromIterable(templates)
				.doOnNext(t -> generateFile(t, definition))
				.map(SharedTemplate::getClassName);
	}

	private void generateFile(SharedTemplate template, EntityDefinition definition) {
		Map<String, Object> context = new HashMap<>();
		context.put("entity", definition.getName());
		context.put("entityLowerCase", Utils.unCapitalize(definition.getName()));
		context.put("fields", SwiftFieldTransformer.transform(definition.getFields(), definition.getName()));
		String content = templateEngine.render(template.getTemplatePath(), context);
		fileWriterService.write(template.getOutput(), template.getClassName() + template.getExt(), content);
	}
}
