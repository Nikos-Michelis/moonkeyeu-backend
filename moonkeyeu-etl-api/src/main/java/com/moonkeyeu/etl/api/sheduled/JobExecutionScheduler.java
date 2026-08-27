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

    //@Profile("prod")
    @Scheduled(cron = "0 0 2-22/2 * * *")
    public void scheduledDailyJob() {
        jobExecutionDecider.dailyJobExecution();
    }

   // @Profile("prod")
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledMidnightJob() {
        jobExecutionDecider.midnightJobExecution();
    }

    //@Profile("prod")
    //@Scheduled(fixedRate = 10000)
    public void scheduledBulkInsertJob() {
        jobExecutionDecider.bulkInsertJobExecution();
    }
}
