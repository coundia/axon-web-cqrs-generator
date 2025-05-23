package com.groupe2cs.generator.tests.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import com.groupe2cs.generator.tests.shared.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

public class AllGeneratorControllerTest extends BaseIntegrationTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ApplicationContext context;

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        public GroupMainGenerator groupMainGenerator() {
            return Mockito.mock(GroupMainGenerator.class);
        }
    }

    @Test
    void it_should_return_sse_flux_response() {
        GroupMainGenerator generator = context.getBean(GroupMainGenerator.class);

        Flux<ApiResponseDto> fakeFlux = Flux.just(
                ApiResponseDto.builder().code(200).message("Step 1").build(),
                ApiResponseDto.builder().code(200).message("Step 2").build()
        );
        Mockito.when(generator.generateStreaming(Mockito.any()))
                .thenReturn(fakeFlux);

        String json = """
        {"outputDir":"/tmp/output","definition":{
            "name":"Product","table":"products",
            "multiTenant":false,"auditable":false,
            "fields":[{"name":"id","type":"UUID"},{"name":"name","type":"String"}],
            "stack":[]
        }}""";

        webTestClient.post()
                .uri("/api/v1/generator/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_NDJSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
                .returnResult(ApiResponseDto.class)
                .getResponseBody()
                .collectList()
                .block()
                .forEach(r -> System.out.println("✅ Received: " + r.getMessage()));
    }
}
