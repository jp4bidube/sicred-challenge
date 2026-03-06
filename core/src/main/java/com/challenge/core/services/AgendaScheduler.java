package com.challenge.core.services;

import com.challenge.core.model.Agenda;
import com.challenge.core.model.AgendaStatus;
import com.challenge.core.repositories.AgendaRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AgendaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AgendaScheduler.class);
    private final AgendaService agendaService;
    private final AgendaRepository agendaRepository;
    private final StringRedisTemplate redisTemplate;
    private final AgendaEventProducer agendaEventProducer;

    @Scheduled(fixedRateString = "${agenda.scheduler.interval}")
    public void closeExpiredSessions() {
        log.info("Checking for expired sessions...");
        List<Agenda> expiredAgendas = agendaService.findOpenSessionsExpired();

        if (expiredAgendas.isEmpty()) {
            log.info("No expired sessions found.");
            return;
        }

        for (Agenda agenda : expiredAgendas) {
            try {
                log.info("Closing session for agenda: {}", agenda.getId());
                agenda.setStatus(AgendaStatus.CLOSED);
                agendaRepository.save(agenda);

                String key = "agenda:" + agenda.getId() + ":status";
                redisTemplate.delete(key);

                agendaEventProducer.sendAgendaClosedEvent(agenda.getId());
                
                log.info("Session closed successfully for agenda: {}", agenda.getId());
            } catch (Exception e) {
                log.error("Error closing session for agenda: {}", agenda.getId(), e);
            }
        }
    }
}
