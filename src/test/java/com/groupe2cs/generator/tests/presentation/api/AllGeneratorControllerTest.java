package com.groupe2cs.generator.tests.presentation.api;

import com.groupe2cs.generator.application.dto.ApiResponseDto;
import com.groupe2cs.generator.application.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.application.usecase.GroupMainGenerator;
import com.groupe2cs.generator.tests.shared.BaseIntegrationTests;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;


public class AllGeneratorControllerTest extends BaseIntegrationTests {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ApplicationContext context;

    @TestConfiguration
    static class MockConfig {
        @Bean
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

        EntityDefinition entity = new EntityDefinition();
        entity.setName("Product");
        entity.setFields(List.of(
                new FieldDefinition("id", "UUID"),
                new FieldDefinition("name", "String")
        ));

        EntityDefinitionDTO dto = new EntityDefinitionDTO();
        dto.setOutputDir("/tmp/output");
        dto.setDefinition(entity);

        webTestClient.post()
                .uri("/api/v1/generator/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_NDJSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON_VALUE)
                .returnResult(ApiResponseDto.class)
                .getResponseBody()
                .collectList()
                .block()
                .forEach(r -> System.out.println("✅ Received: " + r.getMessage()));
    }

}
