package com.synchftp.main;

import com.synchftp.service.CalloutService;
import com.synchftp.service.FTPUtil;
import com.synchftp.service.SynchUtil;
import org.quartz.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * Created by bhadz on 01.02.2018.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.synchftp.controller","com.synchftp.service"})
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public Scheduler scheduler() throws Exception {
        Properties properties = new Properties();
        properties.put("org.quartz.scheduler.instanceName","QuartzJobScheduler");
        properties.put("org.quartz.jobStore.class","org.quartz.simpl.RAMJobStore");
        properties.put("org.quartz.threadPool.class","org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount","1");
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.afterPropertiesSet();
        Scheduler scheduler = schedulerFactoryBean.getObject();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public CalloutService calloutService(){
        return new CalloutService();
    }

    @Bean
    public FTPUtil ftpUtil(){
        return new FTPUtil();
    }

    @Bean
    public SynchUtil synchUtil(){
        return new SynchUtil();
    }
}
