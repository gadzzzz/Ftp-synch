package com.synchftp.service;

import com.synchftp.model.Settings;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by bhadz on 05.02.2018.
 */
public class FTPUtil {

    public FTPFile[] directoryList(FTPClient ftpClient, String path) throws IOException {
        FTPFile[] fileList = ftpClient.listFiles(path);
        return fileList;
    }

    public boolean createConnection(Settings settings,FTPClient ftpClient) throws IOException {
        ftpClient.connect(settings.getUrl(), settings.getPort());
        boolean isLogged = false;
        if(ftpClient.isConnected()) {
            isLogged = ftpClient.login(settings.getUsername(), settings.getPassword());
            if(isLogged){
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            }
        }
        return isLogged;
    }

    public boolean isFileExist(FTPClient ftpClient, String fileName) throws IOException{
        InputStream inputStream = ftpClient.retrieveFileStream(fileName);
        int returnCode = ftpClient.getReplyCode();
        if (inputStream == null || returnCode == 550) {
            return false;
        }
        return true;
    }

    public String readFile(FTPClient ftpClient, String file_i) throws IOException {
        BufferedReader reader = null;
        InputStream stream = ftpClient.retrieveFileStream(file_i);
        String line = null;
        String content = "";
        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        while((line = reader.readLine()) != null) {
            content += line;
        }
        ftpClient.completePendingCommand();
        reader.close();
        return content;
    }

    public void closeConnection(FTPClient ftpClient){
        if(ftpClient != null) {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
