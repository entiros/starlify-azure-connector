# Starlify connector for azur api gateway
Exports the azure api details to starlify as Service, System and Flow.

### command to create client credentials for azure
az ad sp create-for-rbac --role Reader


## Dependencies
1. Java-8 +

### spring-boot-starter-web
For exposure of connector etc. on http.



## Start
First clone the project using below link
https://github.com/entiros/starlify-azure-connector-.git

## Configuration
Make sure proper Azure api gateway and starlify url's configured properly in properties file like this

```
starlify:
  url: https://api.starlify.com
azure:
  server:
    authUrl: https://login.microsoftonline.com
    apiUrl: https://management.azure.com

```

Go to cloned location and run below command to start the process
mvn clean spring-boot:run

## import azure api details to Starlify
Use below endpoint to start importing api details to starlify as services, systems and flows

```
	Method : POST
	URL : http://localhost:8080/process/azure
	Body : 
			{
                "clientId":"{{Azure client id}}",
                "clientSecret":"{{Azure client secret}}",
                "tenantId":"{{Azure tenant id}}",
                "subscriptionId":"{{Azure subscription id}}",
                "starlifyKey":"{{Starlify api key}}",
                "networkId":"{{Starlify network id}}"
            }
```

## Output
After successful request submission, you should be able to see all the systems and services from azure in give starlify network.