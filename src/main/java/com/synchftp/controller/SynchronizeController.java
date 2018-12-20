package com.synchftp.controller;
import com.synchftp.connector.api.Connector;
import com.synchftp.connector.impl.FTPConnector;
import com.synchftp.connector.impl.SFTPConnector;
import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.model.SettingList;
import com.synchftp.service.CalloutService;
import com.synchftp.service.FTPUtil;
import com.synchftp.service.SFTPUtil;
import com.synchftp.service.SynchUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static org.quartz.JobBuilder.newJob;

/**
 * Created by bhadz on 01.02.2018.
 */
@RestController
@RequestMapping("/api/v1/")
public class SynchronizeController {

    @Autowired
    @Qualifier("ftpUtil")
    private FTPUtil ftpUtil;

    @Autowired
    @Qualifier("sftpUtil")
    private SFTPUtil sftpUtil;

    @Autowired
    @Qualifier("synchUtil")
    private SynchUtil synchUtil;

    @PostMapping("/store")
    public ResponseEntity<Response> storeFile(@RequestBody Setting setting){
        ResponseEntity<Response> response = ResponseEntity.status(401).body(new Response("Invalid credentials"));
        Connector connector;
        Object util;
        if(setting.getSecured()) {
            connector = new SFTPConnector();
            util = sftpUtil;
        }else{
            connector = new FTPConnector();
            util = ftpUtil;
        }
        response = connector.store(setting,util,response);
        return response;
    }

    @PostMapping("/synch")
    public ResponseEntity<List<Response>> synch(@RequestBody SettingList settingList,@RequestHeader("IsAProduction") boolean isProduction){
        Map<String,Response> responseMap = new HashMap<>();
        for(Setting setting_i : settingList.getManufacturers()){
            responseMap.put(setting_i.getUrl(),new Response("Invalid credentials"));
        }
        for(Setting setting_i : settingList.getManufacturers()) {
            Connector connector;
            Object util;
            if(setting_i.getSecured()) {
                connector = new SFTPConnector();
                util = sftpUtil;
            }else{
                connector = new FTPConnector();
                util = ftpUtil;
            }
            connector.run(setting_i,util,responseMap,synchUtil,isProduction);
        }
        List<Response> responseList = new ArrayList<>();
        for(String url_i : responseMap.keySet()){
            Response response = responseMap.get(url_i);
            response.setURL(url_i);
            responseList.add(response);
        }
        ResponseEntity<List<Response>> response = ResponseEntity.status(200).body(responseList);
        return response;
    }
}
