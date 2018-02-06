package com.synchftp.model;

import java.util.List;
import java.util.Map;

/**
 * Created by bhadz on 05.02.2018.
 */
public class FileSetting {
    private Map<String,String> fileNameToUrl;
    private String path;

    public Map<String, String> getFileNameToUrl() {
        return fileNameToUrl;
    }

    public void setFileNameToUrl(Map<String, String> fileNameToUrl) {
        this.fileNameToUrl = fileNameToUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
