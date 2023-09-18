package com.example.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleTaskRepository extends JpaRepository<ScheduleTask, String> {
}
