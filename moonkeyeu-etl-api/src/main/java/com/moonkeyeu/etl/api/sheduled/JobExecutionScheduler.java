package com.moonkeyeu.etl.api.sheduled;

import com.moonkeyeu.etl.api.service.impl.job.JobExecutionDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobExecutionScheduler {
    private final JobExecutionDecider jobExecutionDecider;

    //@Scheduled(cron = "0 0/110 1-22 * * *")
    public void scheduledDailyJob() {
        jobExecutionDecider.dailyJobExecution();
    }

    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void scheduledMidnightJob() {
        jobExecutionDecider.midnightJobExecution();
    }

    //@Scheduled(fixedRate = 10000)
    @Profile("dev")
    public void scheduledBulkInsertJob() {
        jobExecutionDecider.bulkInsertJobExecution();
    }
}
