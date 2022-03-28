package com.entiros.starlify.azureconnector.api.dto.wso2;


import lombok.Data;

@Data
public class Pagination{
    public int total;
    public int offset;
    public int limit;
}