package com.example.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ScheduleTaskJdbcRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public void save(ScheduleTask scheduleTask) {
        String sql = "INSERT INTO schedule_task (id ,job_name, execute_time, status, created_at, updated_at) " +
                "VALUES (:id, :jobName, :executeTime, :jobStatus, :createdAt, :updatedAt) ";
        try {
            namedParameterJdbcTemplate.update(sql, changeToSqlParameterSource(scheduleTask));
        } catch (Exception e) {
            log.info("이미 실행중인 작업입니다.");
        }
    }

    private MapSqlParameterSource changeToSqlParameterSource(ScheduleTask scheduleTask) {
        LocalDateTime now = LocalDateTime.now();
        return new MapSqlParameterSource()
                .addValue("id", scheduleTask.getId())
                .addValue("jobName", scheduleTask.getJobName())
                .addValue("executeTime", scheduleTask.getExecuteTime())
                .addValue("jobStatus", scheduleTask.getStatus().name())
                .addValue("createdAt", now)
                .addValue("updatedAt", now);
    }

    public void updateById(String id, JobStatus status) {
        String sql = "UPDATE schedule_task SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }
}
