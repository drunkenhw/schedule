package com.example.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
public class ScheduleTask extends BaseEntity {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Id
    private String id;

    private String jobName;

    private LocalDateTime executeTime;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    protected ScheduleTask() {
    }

    public ScheduleTask(String jobName, LocalDateTime executeTime, JobStatus status) {

        this.id = formatter.format(executeTime) + jobName;
        this.jobName = jobName;
        this.executeTime = executeTime;
        this.status = status;
    }
}
