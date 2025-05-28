package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
//import com.groupe2cs.generator.application.usecase.AndroidGeneratorService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class AndroidGeneratorController {

//	private final AndroidGeneratorService androidGeneratorService;

	@PostMapping("/android")
	public ApiResponseDto generate(@RequestBody EntityDefinitionDTO request) {
		String output = request.getOutputDir() + "/" + request.getDefinition().getName();
		EntityDefinition entityDefinition = request.getDefinition();

		List<FieldDefinition> fields = new ArrayList<>(entityDefinition.getFields());
		//entityDefinition.addDefaultFieldIfMissing();
		entityDefinition.setFields(fields);

		//androidGeneratorService.generate(entityDefinition, output);
		return ApiResponseDto.ok("Android code generated");
	}
}
