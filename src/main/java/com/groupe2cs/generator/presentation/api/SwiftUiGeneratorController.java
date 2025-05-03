package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.usecase.SwiftUiGeneratorService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class SwiftUiGeneratorController {

	private final SwiftUiGeneratorService swiftUiGeneratorService;

	@PostMapping(value = "/swiftui", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<String> generate(@RequestBody EntityDefinitionDTO request) {
		String output = request.getOutputDir() + "/"+ request.getDefinition().getName();

		//add refrence and updateAt
		EntityDefinition entityDefinition = request.getDefinition();

		List<FieldDefinition> fields = new ArrayList<>(
				entityDefinition.getFields()
		);


		if(!entityDefinition.hasField("updatedAt")){
			FieldDefinition updatedAt = FieldDefinition
					.builder()
					.name("updatedAt")
					.type("Date")
					.readOnly(true)
					.nullable(true)
					.build();
			fields.add(updatedAt);
		}

		if(!entityDefinition.hasField("reference")){
			FieldDefinition reference = FieldDefinition
					.builder()
					.name("reference")
					.type("String")
					.readOnly(true)
					.nullable(true)
					.build();
			fields.add(reference);
		}

		entityDefinition.setFields(fields);


		return swiftUiGeneratorService.generate(entityDefinition, output);
	}
}
