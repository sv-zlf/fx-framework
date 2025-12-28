package com.fxly.demo.system.quartz;

import jakarta.annotation.Resource;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * Quartz配置类
 */
@Configuration
public class QuartzConfig {

    @Resource
    private DataSource dataSource;

    /**
     * Spring Bean JobFactory
     * 支持依赖注入到Quartz Job中
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory();
    }

    /**
     * Quartz调度器工厂Bean
     * 设置autoStartup=true实现自启动服务
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(SpringBeanJobFactory springBeanJobFactory) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        
        if (dataSource != null) {
            factoryBean.setDataSource(dataSource);
        }

        // 自启动服务
        factoryBean.setAutoStartup(true);
        
        // 覆盖已存在的任务
        factoryBean.setOverwriteExistingJobs(true);
        
        // 设置JobFactory
        factoryBean.setJobFactory(springBeanJobFactory);
        
        // 启动延迟（秒）
        factoryBean.setStartupDelay(10);
        
        // 上下文加载器继承
        factoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        
        // 等待任务完成后再关闭
        factoryBean.setWaitForJobsToCompleteOnShutdown(true);
        
        return factoryBean;
    }

    /**
     * Scheduler Bean
     * 从SchedulerFactoryBean获取Scheduler实例
     */
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }

}
