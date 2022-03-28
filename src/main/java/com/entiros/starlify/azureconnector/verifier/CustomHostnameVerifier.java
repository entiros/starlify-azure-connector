package com.entiros.starlify.azureconnector.verifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class CustomHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
