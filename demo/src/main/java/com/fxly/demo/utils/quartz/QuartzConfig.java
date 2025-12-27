package com.fxly.demo.utils.quartz;

import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * Quartz配置类
 * @author admin
 */
@Configuration
public class QuartzConfig {

    @Resource
    private DataSource dataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        
        if (dataSource != null) {
            factoryBean.setDataSource(dataSource);
        }

        factoryBean.setAutoStartup(true);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setStartupDelay(10);
        
        return factoryBean;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }

}
