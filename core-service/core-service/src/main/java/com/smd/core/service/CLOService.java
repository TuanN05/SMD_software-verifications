package com.smd.core.service;

import com.smd.core.dto.CLORequest;
import com.smd.core.dto.CLOResponse;
import com.smd.core.entity.CLO;
import com.smd.core.entity.CLOPLOMapping;
import com.smd.core.entity.Syllabus;
import com.smd.core.exception.DuplicateResourceException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.CLOPLOMappingRepository;
import com.smd.core.repository.CLORepository;
import com.smd.core.repository.SyllabusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CLOService {
    private final CLORepository cloRepository;
    private final SyllabusRepository syllabusRepository;
    private final CLOPLOMappingRepository mappingRepository;

    @Transactional(readOnly = true)
    public List<CLO> getAllCLOs() {
        return cloRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CLO getCLOById(Long id) {
        return cloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CLO not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public CLOResponse getCLOWithMappings(Long id) {
        CLO clo = getCLOById(id);
        List<CLOPLOMapping> mappings = mappingRepository.findByClo_CloId(id);
        
        List<CLOResponse.PLOMappingSummary> ploMappings = mappings.stream()
            .map(m -> CLOResponse.PLOMappingSummary.builder()
                .ploId(m.getPlo().getPloId())
                .ploCode(m.getPlo().getPloCode())
                .mappingLevel(m.getMappingLevel().name())
                .build())
            .collect(Collectors.toList());
        
        return CLOResponse.builder()
            .cloId(clo.getCloId())
            .syllabusId(clo.getSyllabus().getSyllabusId())
            .cloCode(clo.getCloCode())
            .cloDescription(clo.getCloDescription())
            .ploMappings(ploMappings)
            .build();
    }

    @Transactional(readOnly = true)
    public List<CLO> getCLOsBySyllabusId(Long syllabusId) {
        // Validate if Syllabus exists
        if (!syllabusRepository.existsById(syllabusId)) {
            throw new ResourceNotFoundException("Syllabus not found with id: " + syllabusId);
        }
        return cloRepository.findBySyllabus_SyllabusId(syllabusId);
    }

    @Transactional
    public CLO createCLO(CLORequest request) {
        Syllabus syllabus = syllabusRepository.findById(request.getSyllabusId())
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found with id: " + request.getSyllabusId()));
        
        // Check for duplicate CLO code in the same syllabus
        cloRepository.findByCloCodeAndSyllabus_SyllabusId(request.getCloCode(), request.getSyllabusId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("CLO", "cloCode", request.getCloCode());
                });
        
        CLO clo = CLO.builder()
                .syllabus(syllabus)
                .cloCode(request.getCloCode())
                .cloDescription(request.getCloDescription())
                .build();
        
        return cloRepository.save(clo);
    }

    @Transactional
    public CLO updateCLO(Long id, CLORequest request) {
        CLO clo = getCLOById(id);
        
        Syllabus syllabus = syllabusRepository.findById(request.getSyllabusId())
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus not found with id: " + request.getSyllabusId()));
        
        // Check for duplicate CLO code in the same syllabus (excluding current CLO)
        if (!clo.getCloCode().equals(request.getCloCode())) {
            cloRepository.findByCloCodeAndSyllabus_SyllabusId(request.getCloCode(), request.getSyllabusId())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("CLO", "cloCode", request.getCloCode());
                    });
        }
        
        clo.setCloCode(request.getCloCode());
        clo.setCloDescription(request.getCloDescription());
        clo.setSyllabus(syllabus);
        
        return cloRepository.save(clo);
    }

    @Transactional
    public void deleteCLO(Long id) {
        if (!cloRepository.existsById(id)) {
            throw new ResourceNotFoundException("CLO not found with id: " + id);
        }
        cloRepository.deleteById(id);
    }
}
