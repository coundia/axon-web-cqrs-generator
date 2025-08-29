package com.groupe2cs.generator.domain.model;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import reactor.core.publisher.Flux;

public interface Generator {
	Flux<ApiResponseDto> generate(EntityDefinitionDTO definitionDto);
}
