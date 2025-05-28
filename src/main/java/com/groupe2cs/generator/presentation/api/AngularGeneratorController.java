package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.usecase.AngularGeneratorService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class AngularGeneratorController {

	private final AngularGeneratorService angularGeneratorService;

	@PostMapping("/angular")
	public ApiResponseDto generate(@RequestBody EntityDefinitionDTO request) {
		String output = request.getOutputDir() + "/" + Utils.unCapitalize(request.getDefinition().getName());
		EntityDefinition entityDefinition = request.getDefinition();

		List<FieldDefinition> fields = new ArrayList<>(entityDefinition.getFields());

		entityDefinition.setFields(fields);


		angularGeneratorService.generate(entityDefinition, output);
		return ApiResponseDto.ok("Angular code generated");
	}
}
