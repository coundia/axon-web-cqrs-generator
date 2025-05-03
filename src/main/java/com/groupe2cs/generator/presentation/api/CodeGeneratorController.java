package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.domain.model.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final Generator generator;

    @PostMapping(value = "/all", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ApiResponseDto> generate(@RequestBody EntityDefinitionDTO request) {


        return generator.generate(request);
    }
}
