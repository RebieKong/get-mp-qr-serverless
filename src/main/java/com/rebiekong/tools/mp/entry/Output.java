package com.rebiekong.tools.mp.entry;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class Output {
    public boolean isBase64Encoded = false;
    public int statusCode = 200;
    public Map<String, String> headers = new HashMap<>();
    public String body = "";

    public String toString(){
        return JSON.toJSONString(this);
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    public void setBase64Encoded(boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
