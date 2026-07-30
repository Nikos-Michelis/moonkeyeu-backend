package com.moonkeyeu.core.api.launch.scheduled;

import com.moonkeyeu.core.api.launch.services.NasaApodService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledNasaApodTasks {

    private final NasaApodService nasaApodService;

    @Scheduled(cron = "0 0 */4 * * *", initialDelay = 1000)
    public void refreshNasaApodCache(){
       nasaApodService.refreshNasaApod();
    }
}
