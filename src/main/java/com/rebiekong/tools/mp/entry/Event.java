package com.rebiekong.tools.mp.entry;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Event {
    public String path = null;
    public String httpMethod = null;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> queryParameters = new HashMap<>();
    public Map<String, String> pathParameters = new HashMap<>();
    public String body = "";
    public boolean isBase64Encoded = false;

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public String getBody() {
        if (isBase64Encoded) {
            return new String(Base64.getDecoder().decode(body));
        } else {
            return body;
        }
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }
}
