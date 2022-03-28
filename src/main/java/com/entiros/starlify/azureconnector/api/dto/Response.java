package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
    private List<Link> links;
    private List<T> content;
    private Page page;
}
