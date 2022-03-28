package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class NetworkSystem extends BaseDto {
    private List<Reference> references;
}
