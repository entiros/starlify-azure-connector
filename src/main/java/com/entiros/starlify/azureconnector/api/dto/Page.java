package com.entiros.starlify.azureconnector.api.dto;

import lombok.Data;

@Data
public class Page {
    private int size;
    private long totalElements;
    private int totalPages;
    private int number;
}
