# Starlify connector for Azure API Gateway
Exports Azure API Gateway details to Starlify as systems and services.

### Command to create client credentials for Azure
az ad sp create-for-rbac --role Reader


## Dependencies
1. Java-8 +
2. Maven

### spring-boot-starter-web
For exposure of connector etc. on http.

## Start
Start by cloning the project by using the link below:  
https://github.com/entiros/starlify-azure-connector-.git

## Configuration
Put the text below in your property file to configure your URL for Azure API Gateway and Starlify:

```
starlify:
	url: https://api.starlify.com
azure:
	server:
		authUrl: https://login.microsoftonline.com
		apiUrl: https://management.azure.com

```

Go to cloned location and run the command below to start the process:
```
mvn clean spring-boot:run
```

## Import Azure API Gateway details to Starlify
Use the endpoint below to start importing API details to Starlify as systems and services:
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
After successful request submission, you should be able to see all the systems and services from Azure in your Starlify network.
