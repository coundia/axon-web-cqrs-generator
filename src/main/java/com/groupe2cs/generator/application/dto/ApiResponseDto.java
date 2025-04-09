package com.groupe2cs.generator.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder

public class ApiResponseDto implements Serializable {
    private int code;
    private String message;
    private Object data;

    @Override
    public String toString() {
        return message;
    }

}
