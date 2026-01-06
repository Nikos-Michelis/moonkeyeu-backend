package com.moonkeyeu.etl.api.configuration.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

@Slf4j
public class StepContextSetter<T> implements StepExecutionListener {

    private final String key;
    private final T value;

    /**
     * @param key   Key to store in ExecutionContext
     * @param value Value to store (can be Object or primitive wrapper)
     */
    public StepContextSetter(String key, T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getExecutionContext();
        context.put(key, value);
        log.debug("Set key '{}' in step execution context with value: {}", key, value);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
