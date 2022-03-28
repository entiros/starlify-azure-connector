package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

@Data
public class RefDto {
    private String id;
    private String name;
    private ServiceDto service;
}
