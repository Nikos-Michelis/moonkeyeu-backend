package com.moonkeyeu.core.api.membership.subscription.service.impl;

import com.moonkeyeu.core.api.membership.subscription.dto.PlanDTO;
import com.moonkeyeu.core.api.utils.DtoConverter;
import com.moonkeyeu.core.api.membership.domain.model.SubscriptionPlan;
import com.moonkeyeu.core.api.membership.domain.repository.PlanRepository;
import com.moonkeyeu.core.api.membership.subscription.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final DtoConverter dtoConverter;

    public List<PlanDTO> getAllPlans() {
        List<SubscriptionPlan> subscriptionPlan = planRepository.findAll();
        return subscriptionPlan.stream().map(plan -> dtoConverter.convertToDto(plan, PlanDTO.class)).toList();
    }
}
