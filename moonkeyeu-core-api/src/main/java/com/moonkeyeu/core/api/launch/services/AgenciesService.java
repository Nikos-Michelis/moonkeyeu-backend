package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.utils.DTOEntity;

import java.util.List;
import java.util.Optional;

public interface AgenciesService {
    Optional<DTOEntity> getAgencyById(Integer agencyId);
    List<DTOEntity> getAllAgencies();
}
