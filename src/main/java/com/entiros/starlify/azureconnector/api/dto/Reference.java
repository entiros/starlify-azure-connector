package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

@Data
public class Reference extends BaseDto {
    private NetworkSystem consumingNetworkSystem;
    private Service service;
}
