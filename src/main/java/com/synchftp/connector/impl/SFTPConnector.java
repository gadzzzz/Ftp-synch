package com.synchftp.connector.impl;

import com.synchftp.connector.api.Connector;
import com.synchftp.model.InMemoryFile;
import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.service.SFTPUtil;
import com.synchftp.service.SynchUtil;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Map;

public class SFTPConnector implements Connector {

    @Override
    public void run(Setting setting, Object util, Map<String, Response> responseMap, SynchUtil synchUtil, boolean isProduction) {
        SFTPUtil sftpUtil = (SFTPUtil) util;
        SSHClient sshClient = new SSHClient();
        try {
            SFTPClient sftpClient = sftpUtil.createConnection(setting,sshClient);
            if(sshClient.isConnected()) {
                synchUtil.scheduleJob(sftpClient, setting, isProduction);
                responseMap.put(setting.getUrl(), new Response());
            }else{
                sftpUtil.closeConnection(sftpClient,sshClient);
            }
        }catch (Exception e){
            responseMap.put(setting.getUrl(), new Response(e.getMessage()));
            sftpUtil.closeConnection(null,sshClient);
        }
    }

    @Override
    public ResponseEntity<Response> store(Setting setting, Object util, ResponseEntity<Response> response) {
        SFTPUtil sftpUtil = (SFTPUtil) util;
        SSHClient sshClient = new SSHClient();
        try {
            SFTPClient sftpClient = sftpUtil.createConnection(setting,sshClient);
            if(sshClient.isConnected()) {
                if(!sftpUtil.isFileExist(sftpClient,setting.getPath())){
                    response = ResponseEntity.status(404).body(new Response("Directory not exist"));
                }else {
                    if(sftpUtil.isFileExist(sftpClient,setting.getPath()+"/"+setting.getFileName())) {
                        response = ResponseEntity.status(409).body(new Response("File already exist"));
                    }else{
                        String contentRaw = setting.getContentRaw();
                        byte[] fileContent = Base64.getDecoder().decode(contentRaw);
                        sftpClient.put(new InMemoryFile(setting.getFileName(),fileContent), setting.getPath());
                        response = ResponseEntity.status(200).body(new Response());
                    }
                }
            }else{
                sftpUtil.closeConnection(sftpClient,sshClient);
            }
        }catch (Exception e){
            sftpUtil.closeConnection(null,sshClient);
            response = ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
        return response;
    }
}
