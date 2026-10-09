package com.moonkeyeu.etl.api.configuration.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;

/**
 * @param key   Key to store in ExecutionContext
 * @param value Value to store (can be Object or primitive wrapper)
 */
@Slf4j
public record StepContextSetter<T>(String key, T value) implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getExecutionContext();
        context.put(key, value);
    }
}
