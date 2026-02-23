package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.DTOEntity;

import java.util.List;

public interface AgenciesService {
    DTOEntity getAgencyById(Integer agencyId);
    List<DTOEntity> getAllAgencies();
}
