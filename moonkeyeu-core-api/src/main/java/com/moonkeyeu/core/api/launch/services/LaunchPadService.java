package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.utils.DTOEntity;
import java.util.Map;
import java.util.Optional;

public interface LaunchPadService {
    Map<String, Object> getAllLaunchPads();
    Optional<DTOEntity> getLaunchPadById(Integer launchPadId);


}
