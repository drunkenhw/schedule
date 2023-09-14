package com.example.schedule;

import java.time.LocalDateTime;

public record SchedulingEvent(
        Runnable runnable,
        String jobName,
        LocalDateTime executionTime
) {
}
