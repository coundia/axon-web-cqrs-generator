package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.service.infrastructureservice.SecurityGeneratorService;
import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final GroupMainGenerator groupMainGenerator;
    private final SecurityGeneratorService securityGeneratorService;

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ApiResponseDto> generate(@RequestBody EntityDefinitionDTO request) {

        Sinks.Many<ApiResponseDto> sink = Sinks.many().unicast().onBackpressureBuffer();

        EntityDefinition definition = request.getDefinition();
        String outputDir = request.getOutputDir();

        definition.getStack().add("sync");

        log.info("📨 Requête reçue pour générer l'entité: {}", definition.getName());
        log.info("📦 Fields: {}", definition.getFields().toString());
        definition.getFields().forEach(
                p->log.info("Field : {} \n ",p.toString())
        );
        log.info("📂 Dossier de sortie: {}", outputDir);
        log.info("📂   table: {}", definition.getTable());

        log.info("📦 Generation de la sécurité");
        securityGeneratorService.generate(definition, outputDir)
                .doOnNext(msg -> sink.tryEmitComplete())
                .subscribe();
        log.info("📦 Generation de la sécurité terminée");
        return groupMainGenerator.generateStreaming(request);
    }
}
