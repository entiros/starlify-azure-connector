package com.entiros.starlify.azureconnector.api.service;

import com.entiros.starlify.azureconnector.api.dto.Request;
import com.entiros.starlify.azureconnector.api.dto.RequestItem;

public interface StarlifyExportService {
    RequestItem status(Request request);

    RequestItem submitAzureRequest(Request request);
}
