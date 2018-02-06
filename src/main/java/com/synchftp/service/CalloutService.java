package com.synchftp.service;

import com.synchftp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by bhadz on 05.02.2018.
 */
public class CalloutService {

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    public Response sendFileToSF(String url,String content){
        HttpHeaders sendHeaders = new HttpHeaders();
        sendHeaders.set("Accept","application/xml");
        HttpEntity<?> sendEntity = new HttpEntity<String>(content,sendHeaders);
        ResponseEntity<Response> sendResponse = restTemplate.exchange(url, HttpMethod.POST,sendEntity,Response.class);
        return sendResponse.getBody();
    }
}
