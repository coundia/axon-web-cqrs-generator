package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final GroupMainGenerator groupMainGenerator;

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ApiResponseDto> generate(@RequestBody EntityDefinitionDTO request) {

        log.info("📨 Requête reçue pour générer l'entité: {}", request.getDefinition().getName());
        log.info("📦 Champs: {}", request.getDefinition().getFields().toString());
        log.info("📂 Dossier de sortie: {}", request.getOutputDir());
        log.info("📂   table: {}", request.getDefinition().getTable());


        return groupMainGenerator.generateStreaming(request);
    }
}
