package com.example.demo.kafka;

import com.example.demo.config.TemporalProperties;
import com.example.demo.dto.KafkaEvent;
import com.example.demo.workflows.KafkaEventWorkflow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class KafkaEventListener {

    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;
    private final TemporalProperties props;

    public KafkaEventListener(WorkflowClient workflowClient, TemporalProperties props) {
        this.workflowClient = workflowClient;
        this.objectMapper = new ObjectMapper();
        this.props = props;
    }

    @KafkaListener(
            topics = "common-tools-events",
            groupId = "temporal-worker",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            // Deserializar el JSON
            KafkaEvent event = objectMapper.readValue(record.value(), KafkaEvent.class);
            System.out.println("Evento recibido de Kafka: " + event.getBusinessKey());

            // Map de atributos de búsqueda
            Map<String, Object> searchAttributes = Map.of(
                    "BusinessKey", event.getBusinessKey(),
                    "EventId", event.getEventId()
            );

            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue(props.getTaskQueue())
                    .setWorkflowId("workflow-" + event.getBusinessKey() + "-" + event.getEventId())
                    .setSearchAttributes(searchAttributes)
                    .build();

            KafkaEventWorkflow workflow = workflowClient.newWorkflowStub(KafkaEventWorkflow.class, options);

            // Iniciar workflow asincrónicamente
            WorkflowClient.start(() -> workflow.processEvent(event));

        } catch (JsonProcessingException e) {
            System.err.println("Error al deserializar el evento: " + e.getMessage());
        }
    }
}
