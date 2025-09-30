package com.example.demo.config;

import com.example.demo.activities.EventActivitiesImpl;
import com.example.demo.repositories.EndpointConfigRepository;
import com.example.demo.repositories.RequestTraceRepository;
import com.example.demo.workflows.KafkaEventWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemporalConfig {

    private final TemporalProperties props;

    public TemporalConfig(TemporalProperties props) {
        this.props = props;
    }

    @Bean
    public WorkflowServiceStubs serviceStubs() {
        return WorkflowServiceStubs.newInstance(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(props.getTarget())
                        .build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs stubs) {
        return WorkflowClient.newInstance(stubs);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(destroyMethod = "shutdown")
    public WorkerFactory workerFactory(WorkflowClient client,
                                       EndpointConfigRepository endpointRepo,
                                       RequestTraceRepository traceRepo,
                                       RestTemplate restTemplate) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(props.getTaskQueue());
        worker.registerWorkflowImplementationTypes(KafkaEventWorkflowImpl.class);
        worker.registerActivitiesImplementations(new EventActivitiesImpl(endpointRepo, traceRepo, restTemplate));
        factory.start();
        return factory;
    }
}
