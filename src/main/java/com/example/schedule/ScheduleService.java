package com.example.schedule;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final Queue<SchedulingEvent> tasks = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final ScheduleTaskJdbcRepository scheduleTaskJdbcRepository;
    private final ScheduleTaskRepository scheduleTaskRepository;

    @EventListener
    public void addTask(SchedulingEvent schedulingEvent) {
        tasks.add(schedulingEvent);
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void polling() {
        if (!tasks.isEmpty()) {
            SchedulingEvent schedulingEvent = tasks.poll();
            executorService.execute(() -> execute(schedulingEvent));
        }
    }

    private void execute(SchedulingEvent schedulingEvent) {
        String jobId = schedulingEvent.jobId();
        LocalDateTime executionTime = schedulingEvent.executionTime();

        if (isJobInProgressOrDone(jobId)) {
            log.info("작업이 실행중입니다. {} {}", executionTime, jobId);
            return;
        }

        ScheduleTask entity = new ScheduleTask(jobId, executionTime, JobStatus.RUNNING);
        scheduleTaskJdbcRepository.save(entity);

        try {
            run(schedulingEvent);

            scheduleTaskJdbcRepository.updateById(entity.getId(), JobStatus.DONE);
        } catch (Exception e) {
            log.error("{} 작업 실행 중 에러가 발생했습니다.", jobId);
            scheduleTaskJdbcRepository.updateById(entity.getId(), JobStatus.ERROR);

            tasks.add(schedulingEvent);
        }
    }

    private boolean isJobInProgressOrDone(String jobId) {
        Optional<ScheduleTask> taskOptional = scheduleTaskRepository.findById(jobId);
        if (taskOptional.isPresent()) {
            ScheduleTask scheduleTask = taskOptional.get();
            return scheduleTask.getStatus() == JobStatus.RUNNING || scheduleTask.getStatus() == JobStatus.DONE;
        }
        return false;
    }

    private void run(SchedulingEvent schedulingEvent) {
        schedulingEvent.runnable().run();
    }
}
