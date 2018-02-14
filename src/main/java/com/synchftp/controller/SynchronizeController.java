package com.synchftp.controller;
import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.model.SettingList;
import com.synchftp.service.CalloutService;
import com.synchftp.service.FTPUtil;
import com.synchftp.service.SynchUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Autowired
    @Qualifier("calloutService")
    private CalloutService calloutService;

    @Autowired
    @Qualifier("ftpUtil")
    private FTPUtil ftpUtil;

    @Autowired
    @Qualifier("synchUtil")
    private SynchUtil synchUtil;

    @PostMapping("/store")
    public ResponseEntity<Response> storeFile(@RequestBody Setting setting){
        ResponseEntity<Response> response = ResponseEntity.status(401).body(new Response("Invalid credentials"));
        FTPClient ftpClient = new FTPClient();
        try {
            if (ftpUtil.createConnection(setting, ftpClient)) {
                if(!ftpClient.changeWorkingDirectory(setting.getPath())){
                    response = ResponseEntity.status(404).body(new Response("Invalid credentials"));
                }else{
                    if(ftpUtil.isFileExist(ftpClient,setting.getFileName())) {
                        response = ResponseEntity.status(409).body(new Response("File already exist"));
                    }else{
                        OutputStream outputStream = ftpClient.storeFileStream(setting.getFileName());
                        String contentRaw = setting.getContentRaw();
                        byte[] fileContent = Base64.getDecoder().decode(contentRaw);
                        outputStream.write(fileContent);
                        outputStream.flush();
                        outputStream.close();
                        ftpClient.completePendingCommand();
                        response = ResponseEntity.status(200).body(new Response());
                    }
                }
            }else{
                ftpUtil.closeConnection(ftpClient);
            }
        }catch (Exception e){
            ftpUtil.closeConnection(ftpClient);
            response = ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
        return response;
    }

    @PostMapping("/synch")
    public ResponseEntity<List<Response>> synch(@RequestBody SettingList settingList){
        Map<String,Response> responseMap = new HashMap<>();
        for(Setting setting_i : settingList.getManufacturers()){
            responseMap.put(setting_i.getUrl(),new Response("Invalid credentials"));
        }
        for(Setting setting_i : settingList.getManufacturers()) {
            FTPClient ftpClient = new FTPClient();
            try {
                if (ftpUtil.createConnection(setting_i, ftpClient)) {
                    synchUtil.scheduleJob(ftpClient, setting_i);
                    responseMap.put(setting_i.getUrl(),new Response());
                } else {
                    ftpUtil.closeConnection(ftpClient);
                }
            }catch (Exception e){
                ftpUtil.closeConnection(ftpClient);
                responseMap.put(setting_i.getUrl(),new Response(e.getMessage()));
            }
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
