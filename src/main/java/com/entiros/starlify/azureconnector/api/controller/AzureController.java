package com.entiros.starlify.azureconnector.api.controller;

import com.entiros.starlify.azureconnector.api.dto.*;
import com.entiros.starlify.azureconnector.api.service.StarlifyExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AzureController {

    private final StarlifyExportService starlifyExportService;

    @PostMapping("/status")
    public RequestItem getStatus(@RequestBody Request request) {
        return starlifyExportService.status(request);
    }

    @PostMapping("/process/azure")
    public RequestItem processAzureRequest(@RequestBody Request request) throws ExecutionException, InterruptedException {
        log.info("azure key recieved {}", request.getClientId());
        return starlifyExportService.submitAzureRequest(request);
    }

}

