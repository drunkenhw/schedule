package com.example.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleTaskRepository extends JpaRepository<ScheduleTask, Long> {
    Optional<ScheduleTask> findScheduleTaskByJobNameAndExecuteTime(String jobName, LocalDateTime executeTime);

}
