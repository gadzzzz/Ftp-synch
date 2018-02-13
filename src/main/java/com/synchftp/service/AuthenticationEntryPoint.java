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

/**
 * Created by bhadz on 13.02.2018.
 */
@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AuthenticationException authException) throws IOException, ServletException {
        Response response = new Response(authException.getMessage());
        httpResponse.setHeader("Content-type","application/json");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = httpResponse.getWriter();
        Gson gson = new Gson();
        writer.println(gson.toJson(response));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("ftp-synch");
        super.afterPropertiesSet();
    }
}
