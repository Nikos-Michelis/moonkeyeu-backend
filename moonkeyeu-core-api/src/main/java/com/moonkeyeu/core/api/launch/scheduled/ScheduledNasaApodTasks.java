package com.moonkeyeu.core.api.launch.scheduled;

import com.moonkeyeu.core.api.launch.services.NasaApodService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledNasaApodTasks {

    private final NasaApodService nasaApodService;

    @PostConstruct
    public void initNasaApodCache() {
        refreshNasaApodCache();
    }

    @Scheduled(cron = "0 0 */4 * * *")
    public void refreshNasaApodCache(){
       nasaApodService.refreshNasaApod();
    }
}
