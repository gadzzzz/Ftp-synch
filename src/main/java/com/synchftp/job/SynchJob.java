package com.synchftp.job;

import com.synchftp.model.FileSetting;
import com.synchftp.model.Response;
import com.synchftp.model.Settings;
import com.synchftp.service.CalloutService;
import com.synchftp.service.FTPUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.*;

/**
 * Created by bhadz on 05.02.2018.
 */
public class SynchJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        FTPClient ftpClient = (FTPClient) data.get("ftpClient");
        Settings settings = (Settings) data.get("settings");
        FTPUtil ftpUtil = (FTPUtil) data.get("ftpUtil");
        CalloutService calloutService = (CalloutService) data.get("calloutService");
        try {
            List<FileSetting> fileSettingList = settings.getFileSettingList();
            Map<String,FTPFile[]> pathToFileMap = new HashMap<>();
            for(FileSetting fileSetting_i : fileSettingList){
                pathToFileMap.put(fileSetting_i.getPath(),ftpUtil.directoryList(ftpClient,fileSetting_i.getPath()));
            }
            Map<String,List<String>> fileNameToPath = new HashMap<>();
            for(FileSetting fileSetting_i : fileSettingList){
                if(pathToFileMap.containsKey(fileSetting_i.getPath())){
                    FTPFile[] fileList = pathToFileMap.get(fileSetting_i.getPath());
                    for(FTPFile file_i : fileList){
                        Set<String> fileNameSet = fileSetting_i.getFileNameToUrl().keySet();
                        for(String fileName_i : fileNameSet){
                            if(file_i.getName().startsWith(fileName_i)){
                                if(!fileNameToPath.containsKey(fileSetting_i.getPath()+fileName_i)){
                                    fileNameToPath.put(fileSetting_i.getPath()+fileName_i,new ArrayList<>());
                                }
                                fileNameToPath.get(fileSetting_i.getPath()+fileName_i).add(fileSetting_i.getPath()+"/"+file_i.getName());
                            }
                        }
                    }
                }
            }
            Map<String,Map<String,List<String>>> pathToContentMap = new HashMap<>();
            for(FileSetting fileSetting_i : fileSettingList){
                for(String fileName : fileSetting_i.getFileNameToUrl().keySet()) {
                    if (fileNameToPath.containsKey(fileSetting_i.getPath()+fileName)) {
                        List<String> fileToDownloadList = fileNameToPath.get(fileSetting_i.getPath()+fileName);
                        for (String file_i : fileToDownloadList) {
                            String content = ftpUtil.readFile(ftpClient, file_i);
                            if(!pathToContentMap.containsKey(fileSetting_i.getPath())) {
                                pathToContentMap.put(fileSetting_i.getPath(), new HashMap<String,List<String>>());
                            }
                            if(!pathToContentMap.get(fileSetting_i.getPath()).containsKey(fileName)){
                                pathToContentMap.get(fileSetting_i.getPath()).put(fileName, new ArrayList<>());
                            }
                            pathToContentMap.get(fileSetting_i.getPath()).get(fileName).add(content);
                        }
                    }
                }
            }
            for(FileSetting fileSetting_i : fileSettingList){
                if(pathToContentMap.containsKey(fileSetting_i.getPath())){
                    Map<String,List<String>> contentMap = pathToContentMap.get(fileSetting_i.getPath());
                    for(String fileName_i : contentMap.keySet()){
                        List<String> contentList = contentMap.get(fileName_i);
                        for(String content_i : contentList) {
                            String url = fileSetting_i.getFileNameToUrl().get(fileName_i);
                            //Response response = calloutService.sendFileToSF(url, content_i);
                            //if (response.getSuccess()) {

                            //}
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ftpUtil.closeConnection(ftpClient);
        }
    }
}
