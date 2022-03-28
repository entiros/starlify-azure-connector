package com.entiros.starlify.azureconnector.api.service;

import com.entiros.starlify.azureconnector.api.dto.RequestItem;
import com.entiros.starlify.azureconnector.api.dto.azure.AzureApis;
import com.entiros.starlify.azureconnector.api.dto.wso2.Wso2ApiDetails;

import java.util.List;

public interface AzureService {

    List<AzureApis> getAzureApis(RequestItem accessToken);

    Wso2ApiDetails getWso2ApiDetails(String accessToken, String apiId);

}
