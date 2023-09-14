package com.example.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Table(
        name = "schedule_task",

        uniqueConstraints = {
                @UniqueConstraint(
                        name = "schedule_task_uk",
                        columnNames = {
                                "job_name",
                                "execute_time"
                        }
                ),
        })
@Entity
public class ScheduleTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "job_name")
    private String jobName;

    @Column(name = "execute_time")
    private LocalDateTime executeTime;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    protected ScheduleTask() {
    }

    public ScheduleTask(String jobName, LocalDateTime executeTime, JobStatus status) {
        this.jobName = jobName;
        this.executeTime = executeTime;
        this.status = status;
    }

    public ScheduleTask(Long id, String jobName, LocalDateTime executeTime, JobStatus status) {
        this.id = id;
        this.jobName = jobName;
        this.executeTime = executeTime;
        this.status = status;
    }

    public void updateStatus(JobStatus jobStatus) {
        this.status = jobStatus;
    }

    public Long getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public JobStatus getStatus() {
        return status;
    }
}
