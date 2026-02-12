package com.moonkeyeu.core.api.launch.repository;

import com.moonkeyeu.core.api.ai.model.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AiPromptRepository extends JpaRepository<AiPrompt, Object> {
    @Query("""
           SELECT ap
           FROM AiPrompt ap
           INNER JOIN FETCH ap.launches l
           WHERE l.launchId = :launchId
           ORDER BY ap.createdAt ASC
           LIMIT 1
    """)
    Optional<AiPrompt> findAiPromptByLaunchId(@Param("launchId") String launchId);

    @Query("""
           SELECT ap
           FROM AiPrompt ap
           INNER JOIN FETCH ap.astronauts a
           LEFT JOIN FETCH a.status
           LEFT JOIN FETCH a.socialMedia
           LEFT JOIN FETCH a.crewMembers cm
           LEFT JOIN FETCH a.agency ac
           LEFT JOIN FETCH ac.agencyType
           LEFT JOIN FETCH cm.launch
           LEFT JOIN FETCH cm.spacecraftStage st
           LEFT JOIN FETCH st.spacecraft
           WHERE a.astronautId = :astronautId
           ORDER BY ap.createdAt ASC
           LIMIT 1
    """)
    Optional<AiPrompt> findAiPromptByAstronautId(@Param("astronautId") Long astronautId);


}
