package com.moonkeyeu.core.api.user.services.impl;

import com.moonkeyeu.core.api.utils.caching.CacheNames;
import com.moonkeyeu.core.api.launch.dto.DTOEntity;
import com.moonkeyeu.core.api.launch.dto.paging.PageSortingDTO;
import com.moonkeyeu.core.api.user.dto.ContactDTO;
import com.moonkeyeu.core.api.user.dto.UserDTO;
import com.moonkeyeu.core.api.user.model.*;
import com.moonkeyeu.core.api.user.reporitory.ContactRepository;
import com.moonkeyeu.core.api.user.reporitory.UserRepository;
import com.moonkeyeu.core.api.user.reporitory.specification.MessagesSpecification;
import com.moonkeyeu.core.api.user.services.DashboardService;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final DtoConverter dtoConverter;
    @Autowired
    public DashboardServiceImpl(
            ContactRepository contactRepository,
            UserRepository userRepository,
            DtoConverter dtoConverter
    ) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.dtoConverter = dtoConverter;
    }
    @Override
    @Cacheable(value = CacheNames.CONTACT_MESSAGES_CACHE, key = "'messages-pagination-' + #requestParams + '-' + #pageSortingDTO", sync = true)
    public Page<DTOEntity> searchMessages(Map<String, String> requestParams, PageSortingDTO pageSortingDTO) {
        Specification<Contact> spec = Specification.unrestricted();
        if (requestParams != null && !requestParams.isEmpty()) {

            if (requestParams.containsKey("search") && !requestParams.get("search").isEmpty()) {
                spec = spec.and(MessagesSpecification.hasSearchKey(requestParams.get("search")));
            }
        }

        Sort sortObject = "desc".equalsIgnoreCase(pageSortingDTO.getSort())
                ? Sort.by(pageSortingDTO.getField()).descending()
                : Sort.by(pageSortingDTO.getField()).ascending();

        int page = (pageSortingDTO.getPage() > 0) ? pageSortingDTO.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(page, pageSortingDTO.getLimit(), sortObject);
        Page<Contact> messages = contactRepository.findAll(spec, pageable);
        return messages.map(message -> dtoConverter.convertToDto(message, ContactDTO.class));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheNames.CONTACT_MESSAGES_CACHE, allEntries = true)
    public ContactDTO deleteMessage(Long messageId) {
        Contact contact = contactRepository.findByContactId(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message Not Found."));
        contactRepository.delete(contact);
        return dtoConverter.convertToDto(contact, ContactDTO.class);
    }

    @Override
    @Cacheable(value = CacheNames.MEMBERS_CACHE, key = "'members'", sync = true)
    public List<UserDTO> getAllMembers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(member -> UserDTO.builder()
                        .userId(member.getUserId())
                        .username(member.getNickname())
                        .email(member.getEmail())
                        .role(member.getRoles().stream().map(Roles::getName).collect(Collectors.toSet()))
                        .accountNonLocked(member.isAccountNonLocked())
                        .enabled(member.isEnabled())
                        .createdAt(member.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
