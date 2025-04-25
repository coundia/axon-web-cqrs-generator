package com.groupe2cs.generator.tests.presentation.api;

import com.groupe2cs.generator.application.usecase.SecurityGeneratorService;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.tests.shared.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityGeneratorControllerTest extends BaseIntegrationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private SecurityGeneratorService securityGeneratorService;

	@TestConfiguration
	static class MockSecurityConfig {
		@Bean
		@Primary
		SecurityGeneratorService securityGeneratorService() {
			return Mockito.mock(SecurityGeneratorService.class);
		}
	}

	@Test
	void it_should_return_ndjson_flux_of_strings() {

		Flux<String> fakeFlux = Flux.just(
				"✔️ Generated: SecurityConfig",
				"✔️ Generated: SecurityInitializer"
		);
		Mockito.when(securityGeneratorService.generate(
						Mockito.any(EntityDefinition.class),
						Mockito.anyString()))
				.thenReturn(fakeFlux);

		String payload = """
        {
          "outputDir": "/tmp/output",
          "definition": {
            "name": "User",
            "table": "users",
            "multiTenant": false,
            "auditable": false,
            "stack": [ "Security" ]
          }
        }
        """;


		List<String> results = webTestClient.post()
				.uri("/api/v1/generator/security")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_NDJSON)
				.bodyValue(payload)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
				.returnResult(String.class)
				.getResponseBody()
				.collectList()
				.block();


		assertThat(results)
				.contains(
						"✔\uFE0F Generated: SecurityConfig✔\uFE0F Generated: SecurityInitializer"
				);
	}
}
