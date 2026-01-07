package com.moonkeyeu.etl.api.service.sheduled;

import com.moonkeyeu.etl.api.service.job.JobExecutionDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobExecutionScheduler {
    private final JobExecutionDecider jobExecutionDecider;

    //@Scheduled(cron = "0 0/110 1-22 * * *")
    public void sheduledDailyJob() {
        jobExecutionDecider.dailyJobExecution();
    }

    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */2 * * * *")
    public void sheduledMidnightJob() {
        jobExecutionDecider.midnightJobExecution();
    }

    //@Scheduled(fixedRate = 10000)
    @Profile("dev")
    public void sheduledBulkInsertJob() {
        jobExecutionDecider.bulkInsertJobExecution();
    }
}
