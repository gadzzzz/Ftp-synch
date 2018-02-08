package com.synchftp.model;

/**
 * Created by bhadz on 08.02.2018.
 */
public class Auth {

    private String access_token;
    private String instance_url;
    private String token_type;

    public Auth() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getInstance_url() {
        return instance_url;
    }

    public void setInstance_url(String instance_url) {
        this.instance_url = instance_url;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}