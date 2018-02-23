package com.synchftp.service;

import com.synchftp.model.Auth;
import com.synchftp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by bhadz on 05.02.2018.
 */
public class CalloutService {

    private static final String AUTH = "/services/oauth2/token";
    private static final String REST = "/services/apexrest/";

    @Autowired
    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public Response sendFileToSF(String endpoint,Auth auth, String content){
        HttpHeaders sendHeaders = new HttpHeaders();
        sendHeaders.set("Content-type","application/xml");
        sendHeaders.set("Accept","application/json");
        sendHeaders.set("Authorization","Bearer "+auth.getAccess_token());
        HttpEntity<?> sendEntity = new HttpEntity<String>(content,sendHeaders);
        ResponseEntity<Response> sendResponse = restTemplate.exchange(auth.getInstance_url()+REST+endpoint, HttpMethod.POST,sendEntity,Response.class);
        return sendResponse.getBody();
    }

    public Auth auth(String envPrefix){
        UriComponentsBuilder loginBuilder = UriComponentsBuilder
                .fromHttpUrl("https://"+envPrefix+".salesforce.com" + AUTH)
                .queryParam("grant_type","password")
                .queryParam("client_id",env.getProperty(envPrefix+"_app_client_id"))
                .queryParam("client_secret",env.getProperty(envPrefix+"_app_client_secret"))
                .queryParam("username",env.getProperty(envPrefix+"_app_username"))
                .queryParam("password",env.getProperty(envPrefix+"_app_password")+env.getProperty(envPrefix+"_app_token"));
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.set("Accept","application/json");
        HttpEntity<?> loginEntity = new HttpEntity<String>(loginHeaders);
        ResponseEntity<Auth> authResponse = restTemplate.exchange(loginBuilder.build().encode().toUri(), HttpMethod.POST,loginEntity,Auth.class);
        return authResponse.getBody();
    }
}
