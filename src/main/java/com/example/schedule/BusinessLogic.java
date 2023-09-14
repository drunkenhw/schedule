package com.example.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class BusinessLogic {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0/2 * * * * *")
    public void complexJobSchedule() {
        applicationEventPublisher.publishEvent(new SchedulingEvent(this::complexJob, "complexJob", LocalDateTime.now()));
    }

    @Scheduled(cron = "0/4 * * * * *")
    public void moreComplexJobSchedule() {
        applicationEventPublisher.publishEvent(new SchedulingEvent(this::moreComplexJob, "moreComplexJob", LocalDateTime.now()));
    }

    private void complexJob() {
        log.info("복잡한 Job 시작");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("복잡한 Job 종료");
    }

    private void moreComplexJob() {
        log.info("좀 더 복잡한 Job 시작");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("좀 더 복잡한 Job 종료");
    }
}
