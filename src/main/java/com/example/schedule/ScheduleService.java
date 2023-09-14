package com.example.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleTaskRepository scheduleTaskRepository;
    private final ScheduleTaskJdbcRepository scheduleTaskJdbcRepository;

    @Scheduled(cron = "0/7 * * * * *")
    public void schedule1() {
        LocalDateTime now = LocalDateTime.now();
        ScheduleTask entity = new ScheduleTask("schedule 1", now, JobStatus.RUNNING);
        boolean isRunning = scheduleTaskJdbcRepository.existsByJobNameAndExecuteTime("schedule 1", now);
        if (!isRunning) {
            ScheduleTask saved = scheduleTaskJdbcRepository.save(entity);
            String string = UUID.randomUUID().toString();
            log.info(string);
            extracted(string);
            extracted(saved);
        }
    }

    private void extracted(ScheduleTask saved) {
        try {
            saved.updateStatus(JobStatus.DONE);
            scheduleTaskJdbcRepository.updateById(saved.getId(), saved.getStatus());

        } catch (Exception e) {
            log.info("schedule 1 작업 실패");
        }
    }

    public void extracted(String a) {
        log.info("schedule 1 시작 === 시작 시간 {}, UUID = {}", LocalDateTime.now(), a);
        log.info("schedule 1 작업 종료");
    }
}
