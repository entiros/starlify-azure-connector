package com.entiros.starlify.azureconnector.api.dto.wso2;

import java.util.ArrayList;


import lombok.Data;

@Data
public class Wso2ApiDetails {
    public String id;
    public String name;
    public Object description;
    public String context;
    public String version;
    public String provider;
    public String apiDefinition;
    public Object wsdlUri;
    public String status;
    public boolean isDefaultVersion;
    public ArrayList<String> transport;
    public Object authorizationHeader;
    public String apiSecurity;
    public ArrayList<Object> tags;
    public ArrayList<String> tiers;
    public Object thumbnailUrl;
    public AdditionalProperties additionalProperties;
    public ArrayList<EndpointURL> endpointURLs;
    public BusinessInformation businessInformation;
    public ArrayList<Object> labels;
    public ArrayList<String> environmentList;
}