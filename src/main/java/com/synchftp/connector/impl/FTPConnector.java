package com.synchftp.connector.impl;

import com.synchftp.connector.api.Connector;
import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.service.FTPUtil;
import com.synchftp.service.SynchUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.http.ResponseEntity;

import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;

public class FTPConnector implements Connector {

    @Override
    public void run(Setting setting, Object util, Map<String,Response> responseMap, SynchUtil synchUtil,boolean isProduction) {
        FTPUtil ftpUtil = (FTPUtil) util;
        FTPClient ftpClient = new FTPClient();
        try {
            if (ftpUtil.createConnection(setting, ftpClient)) {
                synchUtil.scheduleJob(ftpClient, setting, isProduction);
                responseMap.put(setting.getUrl(), new Response());
            } else {
                ftpUtil.closeConnection(ftpClient);
            }
        } catch (Exception e) {
            ftpUtil.closeConnection(ftpClient);
            responseMap.put(setting.getUrl(), new Response(e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<Response> store(Setting setting, Object util, ResponseEntity<Response> response) {
        FTPUtil ftpUtil = (FTPUtil) util;
        FTPClient ftpClient = new FTPClient();
        try {
            if (ftpUtil.createConnection(setting, ftpClient)) {
                if(!ftpClient.changeWorkingDirectory(setting.getPath())){
                    response = ResponseEntity.status(404).body(new Response("Directory not exist"));
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
}
