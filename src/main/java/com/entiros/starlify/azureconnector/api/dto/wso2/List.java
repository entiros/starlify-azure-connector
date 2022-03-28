package com.entiros.starlify.azureconnector.api.dto.wso2;

import java.util.ArrayList;

import lombok.Data;

@Data
public class List {
    public String id;
    public String name;
    public String description;
    public String context;
    public String version;
    public String provider;
    public String status;
    public Object thumbnailUri;
    public ArrayList<Object> scopes;
    public Wso2ApiDetails wso2ApiDetails;
}