package com.synchftp.model;

import java.util.List;

/**
 * Created by bhadz on 08.02.2018.
 */
public class Setting {
    private String username;
    private String password;
    private String url;
    private int port;
    private List<FileSetting> fileSettingList;
    private String contentRaw;
    private String fileName;
    private String path;
    private boolean secured;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<FileSetting> getFileSettingList() {
        return fileSettingList;
    }

    public void setFileSettingList(List<FileSetting> fileSettingList) {
        this.fileSettingList = fileSettingList;
    }

    public String getContentRaw() {
        return contentRaw;
    }

    public void setContentRaw(String contentRaw) {
        this.contentRaw = contentRaw;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean getSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }
}
