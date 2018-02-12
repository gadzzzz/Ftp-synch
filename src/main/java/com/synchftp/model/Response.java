package com.synchftp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhadz on 05.02.2018.
 */
public class Response {
    private Boolean success;
    private String message;
    private String URL;

    public Response() {
        this.success = true;
    }

    public Response(String message) {
        this.success = false;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
