package com.example.demo.activities;

import com.example.demo.dto.KafkaEvent;
import com.example.demo.entities.EndpointConfig;
import com.example.demo.repositories.EndpointConfigRepository;
import com.example.demo.repositories.RequestTraceRepository;
import io.temporal.failure.ApplicationFailure;
import org.springframework.web.client.*;

import java.util.Optional;

public class EventActivitiesImpl implements EventActivities {

    private final EndpointConfigRepository endpointRepo;
    private final RestTemplate restTemplate;

    public EventActivitiesImpl(EndpointConfigRepository endpointRepo,
                               RequestTraceRepository traceRepo,
                               RestTemplate restTemplate) {
        this.endpointRepo = endpointRepo;
        this.restTemplate = restTemplate;
    }

    @Override
    public String resolveUrl(String businessKey) {
        try {
            Optional<EndpointConfig> endpointConfig = endpointRepo.findByBusinessKey(businessKey);
            if (endpointConfig.isEmpty()) {
                throw ApplicationFailure.newNonRetryableFailure(
                        "No endpoint for businessKey=" + businessKey, "ConfigNotFound");
            }
            return endpointConfig.get().getUrl();
        } catch (ApplicationFailure af) {
            throw af;
        } catch (RuntimeException e) {
            throw ApplicationFailure.newFailure("DB error: " + e.getMessage(), "DBError", e);
        }
    }

    @Override
    public void sendRequest(KafkaEvent event, String url) {
        try {
            var response = restTemplate.getForEntity(url, String.class);

            int status = response.getStatusCodeValue();
            if (status >= 200 && status < 400) return;
            else throw ApplicationFailure.newFailure("", "", "");

        } catch (HttpServerErrorException e) {
            throw ApplicationFailure.newFailure(
                    "Http 5xx",
                    "Http5xx",
                    e.getCause()
            );
        } catch (HttpClientErrorException e) {
            throw ApplicationFailure.newNonRetryableFailure(
                    "Http 4xx",
                    "Http4xx",
                    e.getCause()
            );
        } catch (RuntimeException e) {
            throw ApplicationFailure.newFailure(
                    "Transport error",
                    "TransportError",
                    e.getCause()
            );
        }
    }
}
