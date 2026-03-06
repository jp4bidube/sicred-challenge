package com.challenge.core.services;

import com.challenge.core.model.events.AgendaClosedEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AgendaEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AgendaEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendAgendaClosedEvent(String agendaId) {
        AgendaClosedEvent event = AgendaClosedEvent.builder()
                .agendaId(agendaId)
                .build();
        
        try {
            kafkaTemplate.send("agenda-closed", event);
            log.info("AgendaClosedEvent sent for agenda: {}", agendaId);
        } catch (Exception e) {
            log.error("Error sending AgendaClosedEvent for agenda: {}", agendaId, e);
        }
    }
}
