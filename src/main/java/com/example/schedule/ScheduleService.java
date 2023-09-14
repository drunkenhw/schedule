package com.example.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleTaskRepository scheduleTaskRepository;
    private final ScheduleTaskJdbcRepository scheduleTaskJdbcRepository;

    @EventListener
    public void schedule1(SchedulingEvent schedulingEvent) {
        LocalDateTime now = schedulingEvent.executionTime();
        ScheduleTask entity = new ScheduleTask(schedulingEvent.jobName(), now, JobStatus.RUNNING);
        boolean isRunning = scheduleTaskJdbcRepository.existsByJobNameAndExecuteTime(entity.getJobName(), now);
        if (!isRunning) {
            ScheduleTask saved = scheduleTaskJdbcRepository.save(entity);
            schedulingEvent.runnable().run();
            updateJobStatus(saved, schedulingEvent.jobName());
        }
    }

    private void updateJobStatus(ScheduleTask saved, String jobName) {
        try {
            saved.updateStatus(JobStatus.DONE);
            scheduleTaskJdbcRepository.updateById(saved.getId(), saved.getStatus());
        } catch (Exception e) {
            log.info(" 작업 실패");
        }
    }
}
