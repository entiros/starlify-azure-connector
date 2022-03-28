package com.entiros.starlify.azureconnector.api.dto.wso2;

import java.util.ArrayList;


import lombok.Data;

@Data
public class Wso2Apis {
    public int count;
    public String next;
    public String previous;
    public ArrayList<List> list;
    public Pagination pagination;
}