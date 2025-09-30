package com.example.demo.workflows;

import com.example.demo.activities.EventActivities;
import com.example.demo.dto.KafkaEvent;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class KafkaEventWorkflowImpl implements KafkaEventWorkflow {

    private final EventActivities dbActivities;
    private final EventActivities httpActivities;

    public KafkaEventWorkflowImpl() {
        RetryOptions dbRetry = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(2))
                .setBackoffCoefficient(1.0)
                .setMaximumAttempts(5)
                .build();

        RetryOptions httpRetry = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(2))
                .setBackoffCoefficient(2.0)
                .setMaximumAttempts(5)
                .setDoNotRetry("Http4xx")
                .build();

        this.dbActivities = Workflow.newActivityStub(EventActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(15))
                        .setRetryOptions(dbRetry)
                        .build());

        this.httpActivities = Workflow.newActivityStub(EventActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(2))
                        .setRetryOptions(httpRetry)
                        .build());
    }

    @Override
    public void processEvent(KafkaEvent event) {
        String url = dbActivities.resolveUrl(event.getBusinessKey());
        httpActivities.sendRequest(event, url);
    }
}
