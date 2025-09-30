package com.example.demo.activities;

import com.example.demo.dto.KafkaEvent;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface EventActivities {

    @ActivityMethod
    String resolveUrl(String businessKey);

    @ActivityMethod
    void sendRequest(KafkaEvent event, String url);
}
