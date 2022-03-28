package com.entiros.starlify.azureconnector.api.dto.wso2;


import lombok.Data;

@Data
public class EndpointURL{
    public String environmentName;
    public String environmentType;
    public EnvironmentURLs environmentURLs;
}