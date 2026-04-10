package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import java.util.Map;

public interface LaunchPadService {
    Map<String, Object> getAllLaunchPads();
    DTOEntity getLaunchPadById(Integer launchPadId);
}
