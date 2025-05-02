package com.groupe2cs.generator.presentation.api;

import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.application.usecase.SwiftUiGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class SwiftUiGeneratorController {

	private final SwiftUiGeneratorService swiftUiGeneratorService;

	@PostMapping(value = "/swiftui", produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<String> generate(@RequestBody EntityDefinitionDTO request) {
		String output = request.getOutputDir() + "/"+ request.getDefinition().getName();
		return swiftUiGeneratorService.generate(request.getDefinition(), output);
	}
}
