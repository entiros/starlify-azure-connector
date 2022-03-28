package com.entiros.starlify.azureconnector.api.service.impl;

import com.entiros.starlify.azureconnector.api.dto.*;
import com.entiros.starlify.azureconnector.api.dto.azure.AzureApis;
import com.entiros.starlify.azureconnector.api.dto.azure.Operations;
import com.entiros.starlify.azureconnector.api.dto.wso2.EndPointsDetails;
import com.entiros.starlify.azureconnector.api.dto.wso2.Wso2Apis;
import com.entiros.starlify.azureconnector.api.service.StarlifyExportService;
import com.entiros.starlify.azureconnector.api.service.StarlifyService;
import com.entiros.starlify.azureconnector.api.service.AzureService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarlifyExportServiceImpl implements StarlifyExportService {


    private final StarlifyService starlifyService;
    private final AzureService azureService;

    private Map<String, Map<String, NetworkSystem>> cachedNetworkSystems = new ConcurrentHashMap<>();
    private Map<String, RequestItem> statusMap = new ConcurrentHashMap<>();

    @Override
    public RequestItem status(Request request) {
        return statusMap.get(request.getNetworkId());
    }

    @Override
    public RequestItem submitAzureRequest(Request request) {
        RequestItem workItem = new RequestItem();
        workItem.setStatus(RequestItem.Status.NOT_STARTED);
        workItem.setStarlifyKey(request.getStarlifyKey());
        workItem.setClientId(request.getClientId());
        workItem.setClientSecret(request.getClientSecret());
        workItem.setTenantId(request.getTenantId());
        workItem.setSubscriptionId(request.getSubscriptionId());
        workItem.setNetworkId(request.getNetworkId());
        statusMap.put(request.getNetworkId(), workItem);
        CompletableFuture.runAsync(() -> {
            try {
                processAzueRequest(workItem);
            } catch (Throwable t) {
                log.error("error while processing request", t);
            }
        });
        return workItem;
    }

    private void processAzueRequest(RequestItem request) {
        ((RequestItem) request).setStatus(RequestItem.Status.IN_PROCESS);


        List<AzureApis> azureApis = azureService.getAzureApis(request);


        List<NetworkSystem> systems = starlifyService.getSystems(request);

        this.populateSystems(request, systems);

        Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());

        for (AzureApis api : azureApis) {
            try {
                log.info("Started def:" + api.getName());
                NetworkSystem networkSystem = existingSystems != null ? existingSystems.get(api.getName()) : null;
                String systemId = null;
                if (networkSystem == null) {
                    SystemDto systemDto = this.createSystemDto(request, api.getName(), "");
                    SystemRespDto systemRespDto = starlifyService.addSystem(request, systemDto);
                    systemId = systemRespDto.getId();
                } else {
                    systemId = networkSystem.getId();
                }
                Response<ServiceRespDto> services = starlifyService.getServices(request, systemId);
                Set<String> serviceNames = this.getServiceNames(services);


                if (api.getOperations() == null || api.getOperations().isEmpty()) {
                    log.info("empty endpoints size :");
                    continue;
                }
                log.info("Endpoints size :" + api.getOperations().size());
                for (Operations operations : api.getOperations()) {
                    try {
                        if (operations.getPath() == null) {
                            log.info("empty details:");
                            continue;
                        }


                        String name = operations.getMethod() + " " + operations.getPath();
                        if (!serviceNames.contains(name)) {
                            ServiceDto dto = new ServiceDto();
                            dto.setName(name);
                            starlifyService.addService(request, dto, systemId);
                        }

                    } catch (Throwable e) {
                        log.error("Error while processing service:" + api.getName(), e);
                    }
                }
                ((RequestItem) request).setStatus(RequestItem.Status.DONE);
                log.info("Started asset:" + api.getName());
            } catch (Throwable t) {
                log.error("Error while processing asset:" + api.getName(), t);
                ((RequestItem) request).setStatus(RequestItem.Status.ERROR);
            }
        }
    }


    private SystemDto createSystemDto(Request request, String name, String description) {
        SystemDto s = new SystemDto();
        String id = UUID.randomUUID().toString();
        s.setId(id);
        s.setName(name);
        Network n = new Network();
        n.setId(request.getNetworkId());
        s.setNetwork(n);
        s.setDescription(description);
        return s;
    }

    private synchronized void populateSystems(Request request, List<NetworkSystem> networkSystems) {
        if (networkSystems != null && !networkSystems.isEmpty()) {
            Map<String, NetworkSystem> existingSystems = cachedNetworkSystems.get(request.getNetworkId());
            if (existingSystems == null) {
                existingSystems = new ConcurrentHashMap<>();
                cachedNetworkSystems.put(request.getNetworkId(), existingSystems);
            }
            for (NetworkSystem ns : networkSystems) {
                existingSystems.put(ns.getName(), ns);
            }
        }
    }

    private synchronized Set<String> getServiceNames(Response<ServiceRespDto> services) {
        List<ServiceRespDto> content = services.getContent();
        Set<String> ret = new HashSet<>();
        if (content != null && !content.isEmpty()) {
            for (ServiceRespDto c : content) {
                ret.add(c.getName());
            }
        }
        return ret;
    }
}
