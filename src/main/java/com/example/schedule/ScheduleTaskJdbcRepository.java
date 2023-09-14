package com.example.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ScheduleTaskJdbcRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public ScheduleTask save(ScheduleTask scheduleTask) {
        String formattedEventTime = scheduleTask.getExecuteTime().format(formatter);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO schedule_task (job_name, execute_time, status, created_at, updated_at) " +
                "VALUES (:jobName, :executeTime, :jobStatus, :createdAt, :updatedAt) ";
        try {
            namedParameterJdbcTemplate.update(sql, changeToSqlParameterSource(scheduleTask, formattedEventTime), keyHolder);
            long id = keyHolder.getKey().longValue();
            return new ScheduleTask(id, scheduleTask.getJobName(), scheduleTask.getExecuteTime(), scheduleTask.getStatus());
        } catch (DuplicateKeyException e) {
            log.info("이미 실행중인 작업입니다.");
            return null;
        }
    }

    private MapSqlParameterSource changeToSqlParameterSource(ScheduleTask scheduleTask, String formattedEventTime) {
        LocalDateTime now = LocalDateTime.now();
        return new MapSqlParameterSource()
                .addValue("jobName", scheduleTask.getJobName())
                .addValue("executeTime", formattedEventTime)
                .addValue("jobStatus", scheduleTask.getStatus().name())
                .addValue("createdAt", now)
                .addValue("updatedAt", now);
    }

    public void updateById(Long id, JobStatus status) {
        String sql = "UPDATE schedule_task SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }

    public boolean existsByJobNameAndExecuteTime(String jobName, LocalDateTime executeTime) {
        String formattedEventTime = executeTime.format(formatter);
        formattedEventTime = formattedEventTime + ".000000";
        String sql = "SELECT EXISTS (SELECT 1 FROM schedule_task WHERE job_name = ? AND execute_time = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, jobName, formattedEventTime);
    }
}
