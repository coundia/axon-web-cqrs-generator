package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.usecase.SecurityGeneratorService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class SecurityGeneratorController {

	private final SecurityGeneratorService securityGeneratorService;

	@PostMapping(value = "/security", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<String> generate(@RequestBody EntityDefinitionDTO request) {
		log.info("📨 Requête reçue pour générer la sécurité");
		log.debug("📦 Fields: {}", request.getDefinition().getAllFields());
		log.debug("📂 Dossier de sortie: {}", request.getOutputDir());
		log.debug("📂 Table: {}", request.getDefinition().getTable());
		log.info("📦 Lancement de la génération de la sécurité");

		EntityDefinition definition = request.getDefinition();

		definition.setFields(new ArrayList<>());

		String output = request.getOutputDir()+"/tmp";

		return securityGeneratorService
				.generate(definition, output)
				.doOnNext(msg    -> log.info("Message généré: {}", msg))
				.doOnError(error -> log.error("Erreur de génération: {}", error.getMessage()))
				.doOnComplete(() -> log.info("Génération de la sécurité terminée"));
	}
}
