package com.moonkeyeu.core.api.launch.repository;

import com.moonkeyeu.core.api.assistant.model.AiUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiUsageRepository extends JpaRepository<AiUsage, String> {
    //Optional<AiUsage> findAiUsageByL(String aLong);
}
