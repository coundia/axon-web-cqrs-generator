package com.groupe2cs.generator.application.dto;

import com.groupe2cs.generator.domain.model.EntityDefinition;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDefinitionDTO implements Serializable {
	private String outputDir;
	private EntityDefinition definition;
}
