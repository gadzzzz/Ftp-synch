package com.synchftp.service;

import com.google.gson.Gson;
import com.synchftp.model.Response;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhadz on 13.02.2018.
 */
@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException authException) throws IOException, ServletException {
        String requestUri = httpRequest.getRequestURI();
        String responseRaw = "";
        List<Response> responseList = new ArrayList<>();
        Response response = new Response(authException.getMessage());
        responseList.add(response);
        Gson gson = new Gson();
        if("/api/v1/synch".equals(requestUri)) {
            responseRaw = gson.toJson(responseList);
        }else if("/api/v1/store".equals(requestUri)){
            responseRaw = gson.toJson(response);
        }
        httpResponse.setHeader("Content-type","application/json");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = httpResponse.getWriter();
        writer.println(responseRaw);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("ftp-synch");
        super.afterPropertiesSet();
    }
}
