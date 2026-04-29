package com.moonkeyeu.core.api.launch.sheduled;

import com.moonkeyeu.core.api.launch.services.NasaApodService;
import com.moonkeyeu.core.api.launch.services.ScheduledTasksService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    private final NasaApodService nasaApodService;

    @PostConstruct
    public void initNasaApodCache() {
        //nasaApodService.refreshNasaApod();
    }

    @Override
    @Scheduled(cron = "0 0 */4 * * *")
    public void refreshNasaApodCache(){
       nasaApodService.refreshNasaApod();
    }
}
