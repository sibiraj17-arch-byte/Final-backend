package com.healthcare.feature.specializations.service;

import com.healthcare.entity.Specialization;
import com.healthcare.exception.DuplicateResourceException;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.feature.specializations.dto.*;
import com.healthcare.feature.specializations.mapper.SpecializationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SpecializationService {

    private final SpecializationMapper specializationRepository;

    public SpecializationService(SpecializationMapper specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public List<SpecializationResponseDTO> getAll() {
        return specializationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SpecializationResponseDTO getById(Long id) {
        return toDTO(specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", "id", id)));
    }

    @Transactional
    public SpecializationResponseDTO create(SpecializationRequestDTO request) {
        if (specializationRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Specialization already exists with name: " + request.getName());
        }

        Specialization specialization = new Specialization();
        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());

        return toDTO(specializationRepository.save(specialization));
    }

    @Transactional
    public SpecializationResponseDTO update(Long id, SpecializationRequestDTO request) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", "id", id));

        specialization.setName(request.getName());
        specialization.setDescription(request.getDescription());

        return toDTO(specializationRepository.save(specialization));
    }

    @Transactional
    public void delete(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization", "id", id));
        specializationRepository.delete(specialization);
    }

    private SpecializationResponseDTO toDTO(Specialization spec) {
        SpecializationResponseDTO dto = new SpecializationResponseDTO();
        dto.setId(spec.getId());
        dto.setName(spec.getName());
        dto.setDescription(spec.getDescription());
        dto.setCreatedAt(spec.getCreatedAt());
        return dto;
    }
}
