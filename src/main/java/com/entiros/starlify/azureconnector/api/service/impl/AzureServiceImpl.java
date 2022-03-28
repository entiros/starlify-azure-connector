package com.entiros.starlify.azureconnector.api.service.impl;


import com.entiros.starlify.azureconnector.api.dto.RequestItem;
import com.entiros.starlify.azureconnector.api.dto.azure.AzureApis;
import com.entiros.starlify.azureconnector.api.dto.azure.Operations;
import com.entiros.starlify.azureconnector.api.dto.wso2.List;
import com.entiros.starlify.azureconnector.api.dto.wso2.Wso2ApiDetails;
import com.entiros.starlify.azureconnector.api.dto.wso2.Wso2Apis;
import com.entiros.starlify.azureconnector.api.service.AzureService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.json.simple.JsonArray;
import org.apache.camel.json.simple.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AzureServiceImpl implements AzureService {


    private final RestTemplate restTemplate;

    @Value("${azure.server.apiUrl}")
    private String apiUrl;


    @Autowired
    ObjectMapper objectMapper;


    @Value("${azure.server.authUrl}")
    private String authUrl;

    @Override
    public java.util.List<AzureApis> getAzureApis(RequestItem requestItem) {

        String accessToken = getAccessToken(requestItem);

        java.util.List<AzureApis> apisList = new ArrayList<AzureApis>();

        JsonObject resourceGroups = getReSourceGroups(requestItem, accessToken);

        if (resourceGroups != null) {
            JsonArray rv = objectMapper.convertValue(resourceGroups.get("value"), JsonArray.class);
            if (rv.size() > 0) {
                for (Object obj : rv) {
                    JsonObject resource = objectMapper.convertValue(obj, JsonObject.class);

                    JsonObject servicesObj = getServices(resource.getString("id"), accessToken);

                    if (servicesObj != null) {
                        JsonArray services = objectMapper.convertValue(servicesObj.get("value"), JsonArray.class);
                        if (services.size() > 0) {
                            for (Object obj2 : services) {
                                JsonObject service = objectMapper.convertValue(obj2, JsonObject.class);

                                JsonObject apisObj = getApis(service.getString("id"), accessToken);

                                if (apisObj != null) {
                                    JsonArray apis = objectMapper.convertValue(apisObj.get("value"), JsonArray.class);
                                    if (apis.size() > 0) {
                                        for (Object obj3 : apis) {
                                            JsonObject api = objectMapper.convertValue(obj3, JsonObject.class);

                                            if (api.getString("name").equalsIgnoreCase("echo-api")) {
                                                continue;
                                            }


                                            AzureApis azureApi = new AzureApis();
                                            azureApi.setName(api.getString("name"));
                                            azureApi.setOperations(new ArrayList<>());

                                            JsonObject operationsObj = getOperations(api.getString("id"), accessToken);

                                            if (operationsObj != null) {
                                                JsonArray operations = objectMapper.convertValue(operationsObj.get("value"), JsonArray.class);
                                                if (operations.size() > 0) {
                                                    for (Object obj4 : operations) {
                                                        JsonObject operation = objectMapper.convertValue(obj4, JsonObject.class);

                                                        JsonObject properties = objectMapper.convertValue(operation.get("properties"), JsonObject.class);

                                                        Operations operations1 = new Operations();
                                                        operations1.setMethod(properties.getString("method"));
                                                        operations1.setPath(properties.getString("urlTemplate"));

                                                        azureApi.getOperations().add(operations1);

                                                    }
                                                }

                                            }

                                            apisList.add(azureApi);


                                        }
                                    }

                                }


                            }
                        }

                    }


                }
            }

        }

        return apisList;
    }

    private JsonObject getOperations(String apiId, String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<JsonObject> response = restTemplate.exchange(apiUrl + apiId + "/operations?api-version=2021-08-01",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<JsonObject>() {
                });
        JsonObject jsonObject = response.getBody();

        return jsonObject;

    }

    private JsonObject getApis(String serviceId, String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<JsonObject> response = restTemplate.exchange(apiUrl + serviceId + "/apis?api-version=2021-08-01",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<JsonObject>() {
                });
        JsonObject jsonObject = response.getBody();

        return jsonObject;

    }

    private JsonObject getServices(String resourceId, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<JsonObject> response = restTemplate.exchange(apiUrl + resourceId + "/providers/Microsoft.ApiManagement/service?api-version=2021-08-01",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<JsonObject>() {
                });
        JsonObject jsonObject = response.getBody();

        return jsonObject;
    }

    private JsonObject getReSourceGroups(RequestItem requestItem, String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<JsonObject> response = restTemplate.exchange(apiUrl + "/subscriptions/" + requestItem.getSubscriptionId() + "/resourceGroups?api-version=2022-01-01",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<JsonObject>() {
                });
        JsonObject jsonObject = response.getBody();

        return jsonObject;
    }

    private String getAccessToken(RequestItem requestItem) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", requestItem.getClientId());
        map.add("client_secret", requestItem.getClientSecret());
        map.add("resource", apiUrl);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<JsonObject> response =
                restTemplate.exchange(authUrl + "/" + requestItem.getTenantId() + "/oauth2/token",
                        HttpMethod.POST,
                        entity,
                        JsonObject.class);

        JsonObject responseObj = response.getBody();

        log.info("AccessToken generated => {}", responseObj.getString("access_token"));

        return responseObj.getString("access_token");

    }

    @Override
    public Wso2ApiDetails getWso2ApiDetails(String accessToken, String apiId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<Wso2ApiDetails> response = restTemplate.exchange(apiUrl + "/store/apis/" + apiId,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<Wso2ApiDetails>() {
                });
        Wso2ApiDetails wso2ApiDetails = response.getBody();
        return wso2ApiDetails;
    }
}
