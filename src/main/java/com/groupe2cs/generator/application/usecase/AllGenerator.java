package com.groupe2cs.generator.application.usecase;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.domain.model.Generator;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllGenerator implements Generator {

	private final GroupMainGenerator groupMainGenerator;
	private final SecurityGeneratorService securityGeneratorService;
	private final MultiTenantGeneratorService multiTenantGeneratorService;
	private final FileManagerGeneratorService fileManagerGeneratorService;

	public Flux<ApiResponseDto> generate(@RequestBody EntityDefinitionDTO request) {
		EntityDefinition definition = request.getDefinition();
		String outputDir = request.getOutputDir();

		List<FieldDefinition> fields = new ArrayList<>(definition.getAllFields());
		definition.getStack().add("sync");
		definition.getStack().add("mail");
		definition.getStack().add("security");
		definition.setMultiTenant(true);
		definition.setAuditable(true);

//		definition.addDefaultFieldIfMissing();

		definition.setFields(fields);

		log.info("📨 Requête reçue pour générer l'entité: {}", definition.getName());
		log.info("📦 Fields: {}", fields);
		fields.forEach(
				p -> log.info("Field : {} \n ", p)
		);
		log.info("📂 Dossier de sortie: {}", outputDir);
		log.info("📂table: {}", definition.getTable());

		Flux<ApiResponseDto> fileManagerFlux = fileManagerGeneratorService.generate(definition, outputDir)
				.then(Mono.just(ApiResponseDto.builder().code(200).message("FileManager done").build()))
				.flux();

		Flux<ApiResponseDto> securityFlux = securityGeneratorService.generate(definition, outputDir)
				.map(msg -> ApiResponseDto.builder().code(200).message(msg).build());

		Flux<ApiResponseDto> multiTenantFlux = multiTenantGeneratorService.generate(definition, outputDir)
				.then(Mono.just(ApiResponseDto.builder().code(200).message("MultiTenant done").build()))
				.flux();

		Flux<ApiResponseDto> groupMainFlux = groupMainGenerator.generateStreaming(request);

		return Flux.merge(fileManagerFlux, securityFlux, multiTenantFlux, groupMainFlux);


	}
}
