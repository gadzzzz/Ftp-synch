package com.synchftp.job;

import com.synchftp.model.Auth;
import com.synchftp.model.FileSetting;
import com.synchftp.model.Response;
import com.synchftp.model.Setting;
import com.synchftp.service.CalloutService;
import com.synchftp.service.SFTPUtil;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.*;

public class SynchSFTPJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        SFTPClient sftpClient = (SFTPClient) data.get("sftpClient");
        Setting settings = (Setting) data.get("settings");
        SFTPUtil sftpUtil = (SFTPUtil) data.get("sftpUtil");
        boolean isProduction = (Boolean) data.get("isProduction");
        CalloutService calloutService = (CalloutService) data.get("calloutService");
        try {
            List<FileSetting> fileSettingRequestList =  settings.getFileSettingList();
            Map<String,Map<String,String>> settingMap = new HashMap<>();
            for(FileSetting setting_i : fileSettingRequestList){
                String path = setting_i.getPath();
                if(!settingMap.containsKey(path)){
                    settingMap.put(path,new HashMap<>());
                }
                settingMap.get(path).put(setting_i.getName(),setting_i.getUrl());
            }
            Map<String,List<RemoteResourceInfo>> pathToFileMap = new HashMap<>();
            for(String path_i : settingMap.keySet()){
                pathToFileMap.put(path_i,sftpUtil.fileList(sftpClient,path_i));
            }
            Map<String,List<String>> fileNameToPath = new HashMap<>();
            for(String path_i : settingMap.keySet()){
                if(pathToFileMap.containsKey(path_i)){
                    List<RemoteResourceInfo> fileList = pathToFileMap.get(path_i);
                    for(RemoteResourceInfo file_i : fileList){
                        Set<String> fileNameSet = settingMap.get(path_i).keySet();
                        for(String fileName_i : fileNameSet){
                            if(file_i.getName().startsWith(fileName_i)){
                                if(!fileNameToPath.containsKey(path_i+fileName_i)){
                                    fileNameToPath.put(path_i+fileName_i,new ArrayList<>());
                                }
                                fileNameToPath.get(path_i+fileName_i).add(path_i+"/"+file_i.getName());
                            }
                        }
                    }
                }
            }
            Map<String,Map<String,Map<String,String>>> pathToContentMap = new HashMap<>();
            for(String path_i : settingMap.keySet()){
                for(String fileName : settingMap.get(path_i).keySet()) {
                    if (fileNameToPath.containsKey(path_i+fileName)) {
                        List<String> fileToDownloadList = fileNameToPath.get(path_i+fileName);
                        for (String file_i : fileToDownloadList) {
                            String content = sftpUtil.readFile(sftpClient, file_i);
                            if(!pathToContentMap.containsKey(path_i)) {
                                pathToContentMap.put(path_i, new HashMap<>());
                            }
                            if(!pathToContentMap.get(path_i).containsKey(fileName)){
                                pathToContentMap.get(path_i).put(fileName, new HashMap<>());
                            }
                            pathToContentMap.get(path_i).get(fileName).put(file_i,content);
                        }
                    }
                }
            }
            String envPrefix = isProduction?"login":"test";
            Auth auth = calloutService.auth(envPrefix);
            for(String path_i : settingMap.keySet()){
                if(pathToContentMap.containsKey(path_i)){
                    Map<String,Map<String,String>> contentMap = pathToContentMap.get(path_i);
                    for(String fileName_i : contentMap.keySet()){
                        Map<String,String> fileNameToContentMap = contentMap.get(fileName_i);
                        for(String fileNameWithPath_i : fileNameToContentMap.keySet()) {
                            String content_i = fileNameToContentMap.get(fileNameWithPath_i);
                            String url = settingMap.get(path_i).get(fileName_i);
                            if(auth!=null) {
                                Response response = calloutService.sendFileToSF(url,auth,content_i);
                                if(response.getSuccess()){
                                    if(isProduction) {
                                        sftpUtil.deleteFile(sftpClient, fileNameWithPath_i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            sftpUtil.closeConnection(sftpClient,null);
        }
    }
}
