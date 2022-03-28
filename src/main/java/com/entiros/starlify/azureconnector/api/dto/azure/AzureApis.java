package com.entiros.starlify.azureconnector.api.dto.azure;

import lombok.Data;

import java.util.List;

@Data
public class AzureApis {
    String name;
    List<Operations> operations;
}
