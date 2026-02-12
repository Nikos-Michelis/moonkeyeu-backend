package com.moonkeyeu.core.api.user.reporitory;

import com.moonkeyeu.core.api.user.model.batch.BatchJobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<BatchJobExecution, Long> {
}
