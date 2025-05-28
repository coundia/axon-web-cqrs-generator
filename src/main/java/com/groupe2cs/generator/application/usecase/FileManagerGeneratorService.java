package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileManagerGeneratorService {

	private final GeneratorProperties generatorProperties;
	private final GroupMainGenerator groupMainGenerator;

	private Mono<Void> createFileManagerEntity(String baseDir, EntityDefinition parentEntityDefinition) {
		EntityDefinition fileManager = EntityDefinition.builder()
				.name("FileManager")
				.table("file_managers")
				.auditable(true)
				.fields(List.of(
						FieldDefinition.builder().name("id").type("String").build(),
						FieldDefinition.builder().name("name").type("String").build(),
						FieldDefinition.builder().name("details").type("String").nullable(true).build(),
						FieldDefinition.builder().name("objectId").type("String").nullable(true).build(),
						FieldDefinition.builder().name("objectName").type("String").nullable(true).build(),
						FieldDefinition.builder().name("originalName").type("String").nullable(true).build(),
						FieldDefinition.builder().name("mimeType").type("String").nullable(true).build(),
						FieldDefinition.builder().name("size").type("Long").nullable(true).build(),
						FieldDefinition.builder().name("path").type("String").nullable(true).build(),
						FieldDefinition.builder().name("uri").type("String").nullable(true).build(),
						FieldDefinition.builder().name("isPublic").type("Boolean").nullable(true).build()
				))
				.build();

		fileManager.setMultiTenant(true);
		fileManager.setIsGenerated(true);

		return groupMainGenerator.generateStreaming(
				EntityDefinitionDTO.builder()
						.outputDir(baseDir)
						.definition(fileManager)
						.build()
		).then();
	}

	public Mono<Void> generate(EntityDefinition definition, String fullDir) {

		String baseDir = Utils.getParent(fullDir) + "/" + generatorProperties.getFileManagerPackage();
		return createFileManagerEntity(baseDir, definition).then();
	}
}
