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
import reactor.core.publisher.Sinks;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllGenerator implements Generator {

	private final GroupMainGenerator groupMainGenerator;
	private final SecurityGeneratorService securityGeneratorService;
	private final MultiTenantGeneratorService multiTenantGeneratorService;

	public Flux<ApiResponseDto> generate(@RequestBody EntityDefinitionDTO request) {

		Sinks.Many<ApiResponseDto> sink = Sinks.many().unicast().onBackpressureBuffer();

		EntityDefinition definition = request.getDefinition();
		String outputDir = request.getOutputDir();

		List<FieldDefinition> fields = definition.getAllFields();

		definition.getStack().add("sync");
		definition.getStack().add("mail");

		log.info("📨 Requête reçue pour générer l'entité: {}", definition.getName());
		log.info("📦 Fields: {}", fields.toString());
		fields.forEach(
				p -> log.info("Field : {} \n ", p.toString())
		);
		log.info("📂 Dossier de sortie: {}", outputDir);
		log.info("📂table: {}", definition.getTable());

		//Module security
		log.info("📦 Generation de la sécurité");
		securityGeneratorService
				.generate(definition, outputDir)
				.doOnNext(msg -> sink.tryEmitComplete())
				.subscribe();

		//Module tenant
		if (definition.getMultiTenant()) {
			log.info("📦 Generation du module multi-tenant");
			multiTenantGeneratorService
					.generate(definition, outputDir)
					.subscribe();
		}

		log.info("📦 Generation de la sécurité terminée");

		return groupMainGenerator.generateStreaming(request);
	}
}
