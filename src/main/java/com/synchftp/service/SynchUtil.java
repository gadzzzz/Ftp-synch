package com.synchftp.service;

import com.synchftp.job.SynchJob;
import com.synchftp.model.Setting;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by bhadz on 05.02.2018.
 */
public class SynchUtil {

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Autowired
    @Qualifier("calloutService")
    private CalloutService calloutService;

    @Autowired
    @Qualifier("ftpUtil")
    private FTPUtil ftpUtil;

    public void scheduleJob(FTPClient ftpClient, Setting settings, boolean isProduction) throws SchedulerException {
        long currentTime = java.util.Calendar.getInstance().getTimeInMillis();
        JobDetail job = newJob(SynchJob.class).withIdentity("sync_rule"+currentTime, "synch_job").build();
        job.getJobDataMap().put("ftpClient", ftpClient);
        job.getJobDataMap().put("settings", settings);
        job.getJobDataMap().put("calloutService", calloutService);
        job.getJobDataMap().put("ftpUtil",ftpUtil);
        job.getJobDataMap().put("isProduction",isProduction);
        TriggerBuilder triggerBuilder = newTrigger().withIdentity("trigger_synch_rule"+currentTime, "synch_job").startNow();
        Trigger trigger = triggerBuilder.build();
        if (!scheduler.checkExists(job.getKey()) && !scheduler.checkExists(trigger.getKey())) {
            scheduler.scheduleJob(job, trigger);
        }
    }
}
