package com.groupe2cs.generator.application.dto;

import com.groupe2cs.generator.domain.model.EntityDefinition;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EntityDefinitionDTO implements Serializable {
    private String outputDir;
    private EntityDefinition definition;
}
