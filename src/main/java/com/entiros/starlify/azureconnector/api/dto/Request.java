package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

@Data
public class Request {
    private String starlifyKey;
    private String clientId;
    private String networkId;
    private String clientSecret;
    private String tenantId;
    private String subscriptionId;
}
