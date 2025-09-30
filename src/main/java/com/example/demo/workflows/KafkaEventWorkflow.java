package com.example.demo.workflows;

import com.example.demo.dto.KafkaEvent;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface KafkaEventWorkflow {
    @WorkflowMethod
    void processEvent(KafkaEvent event);
}
