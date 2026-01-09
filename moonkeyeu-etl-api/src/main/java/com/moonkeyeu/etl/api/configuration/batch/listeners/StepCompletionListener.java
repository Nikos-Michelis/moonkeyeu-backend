package com.moonkeyeu.etl.api.configuration.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepCompletionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Step: {} started...", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Step: {} completed successfully", stepExecution.getStepName());
            return stepExecution.getExitStatus();
        }

        if (stepExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Step: {} failed!", stepExecution.getStepName());
            stepExecution.getFailureExceptions()
                    .forEach(ex -> log.error("Failure reason", ex));
            return ExitStatus.FAILED;
        }

        return stepExecution.getExitStatus();
    }
}
