package com.synchftp.service;

import com.synchftp.model.Setting;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.LocalDestFile;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SFTPUtil {

    public SFTPClient createConnection(Setting settings,SSHClient sshClient) throws IOException {
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.connect(settings.getUrl(),settings.getPort());
        sshClient.authPassword(settings.getUsername(), settings.getPassword());
        SFTPClient sftpClient = sshClient.newSFTPClient();
        return sftpClient;
    }

    public List<RemoteResourceInfo> fileList(SFTPClient sftpClient,String path) throws IOException{
        List<RemoteResourceInfo> remoteResourceInfoList = sftpClient.ls(path);
        return remoteResourceInfoList;
    }

    public boolean isFileExist(SFTPClient sftpClient, String fileName) throws IOException{
        return sftpClient.statExistence(fileName) != null;
    }

    public String readFile(SFTPClient sftpClient,String path) throws IOException{
        BufferedReader reader = null;
        FileSystemFile file = new FileSystemFile("tmp.file");
        sftpClient.get(path,file);
        String line = null;
        String content = "";
        InputStream stream = file.getInputStream();
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                content += line;
            }
        }catch (IOException e){
            stream.close();
        }
        return content;
    }

    public void closeConnection(SFTPClient sftpClient,SSHClient sshClient){
        if(sftpClient != null) {
            try {
                sftpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sshClient != null){
            if(sshClient.isConnected()){
                try {
                    sshClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteFile(SFTPClient sftpClient,String fileName) throws IOException {
        sftpClient.rm(fileName);
    }

}
