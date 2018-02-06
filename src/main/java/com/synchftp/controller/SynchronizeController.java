package com.synchftp.controller;
import com.synchftp.model.Response;
import com.synchftp.model.Settings;
import com.synchftp.service.CalloutService;
import com.synchftp.service.FTPUtil;
import com.synchftp.service.SynchUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Base64;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

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
    public ResponseEntity storeFile(@RequestBody Settings settings){
        ResponseEntity<Response> response = ResponseEntity.status(401).body(new Response("Invalid credentials"));
        FTPClient ftpClient = new FTPClient();
        try {
            if (ftpUtil.createConnection(settings, ftpClient)) {
                if(!ftpClient.changeWorkingDirectory(settings.getPath())){
                    response = ResponseEntity.status(404).body(new Response("Directory not exist"));
                }else{
                    if(ftpUtil.isFileExist(ftpClient,settings.getFileName())) {
                        response = ResponseEntity.status(409).body(new Response("File already exist"));
                    }else{
                        OutputStream outputStream = ftpClient.storeFileStream(settings.getFileName());
                        String contentRaw = settings.getContentRaw();
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
    public ResponseEntity<Response> synch(@RequestBody Settings settings){
        ResponseEntity<Response> response = ResponseEntity.status(401).body(new Response("Invalid credentials"));
        FTPClient ftpClient = new FTPClient();
        try {
            if(ftpUtil.createConnection(settings,ftpClient)){
                synchUtil.scheduleJob(ftpClient,settings);
                response = ResponseEntity.status(200).body(new Response());
            }else{
                ftpUtil.closeConnection(ftpClient);
            }
        }catch (Exception e){
            ftpUtil.closeConnection(ftpClient);
            response = ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
        return response;
    }
}
