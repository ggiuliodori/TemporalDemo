package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "temporal")
public class TemporalProperties {
    private String taskQueue;
    private String target;

    public String getTaskQueue() { return taskQueue; }
    public void setTaskQueue(String taskQueue) { this.taskQueue = taskQueue; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
}
