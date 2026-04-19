package com.smd.core.service;

import com.smd.core.dto.PLORequest;
import com.smd.core.dto.PLOResponse;
import com.smd.core.entity.CLOPLOMapping;
import com.smd.core.entity.PLO;
import com.smd.core.entity.Program;
import com.smd.core.exception.DuplicateResourceException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.CLOPLOMappingRepository;
import com.smd.core.repository.PLORepository;
import com.smd.core.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PLOService {
    private final PLORepository ploRepository;
    private final ProgramRepository programRepository;
    private final CLOPLOMappingRepository mappingRepository;

    @Transactional(readOnly = true)
    public List<PLO> getAllPLOs() {
        return ploRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PLO getPLOById(Long id) {
        return ploRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PLO not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public PLOResponse getPLOWithCoverage(Long id) {
        PLO plo = getPLOById(id);
        List<CLOPLOMapping> mappings = mappingRepository.findByPlo_PloId(id);
        
        Long totalMappedCLOs = mappingRepository.countByPloId(id);
        
        List<PLOResponse.CLOMappingSummary> cloMappings = mappings.stream()
            .map(m -> PLOResponse.CLOMappingSummary.builder()
                .cloId(m.getClo().getCloId())
                .cloCode(m.getClo().getCloCode())
                .syllabusId(m.getClo().getSyllabus().getSyllabusId())
                .courseCode(m.getClo().getSyllabus().getCourse().getCourseCode())
                .mappingLevel(m.getMappingLevel().name())
                .build())
            .collect(Collectors.toList());
        
        return PLOResponse.builder()
            .ploId(plo.getPloId())
            .programId(plo.getProgram().getProgramId())
            .ploCode(plo.getPloCode())
            .ploDescription(plo.getPloDescription())
            .totalMappedCLOs(totalMappedCLOs.intValue())
            .cloMappings(cloMappings)
            .build();
    }

    @Transactional(readOnly = true)
    public List<PLO> getPLOsByProgramId(Long programId) {
        return ploRepository.findByProgram_ProgramId(programId);
    }

    @Transactional
    public PLO createPLO(PLORequest request) {
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + request.getProgramId()));
        
        // Check for duplicate PLO code in the same program
        ploRepository.findByPloCodeAndProgram_ProgramId(request.getPloCode(), request.getProgramId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("PLO", "ploCode", request.getPloCode() + " in program: " + program.getProgramName());
                });
        
        PLO plo = PLO.builder()
                .program(program)
                .ploCode(request.getPloCode())
                .ploDescription(request.getPloDescription())
                .build();
        
        return ploRepository.save(plo);
    }

    @Transactional
    public PLO updatePLO(Long id, PLORequest request) {
        PLO plo = getPLOById(id);
        
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + request.getProgramId()));
        
        // Check for duplicate PLO code in the same program (excluding current PLO)
        if (!plo.getPloCode().equals(request.getPloCode())) {
            ploRepository.findByPloCodeAndProgram_ProgramId(request.getPloCode(), request.getProgramId())
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("PLO", "ploCode", request.getPloCode() + " in program: " + program.getProgramName());
                    });
        }
        
        plo.setPloCode(request.getPloCode());
        plo.setPloDescription(request.getPloDescription());
        plo.setProgram(program);
        
        return ploRepository.save(plo);
    }

    @Transactional
    public void deletePLO(Long id) {
        if (!ploRepository.existsById(id)) {
            throw new ResourceNotFoundException("PLO not found with id: " + id);
        }
        ploRepository.deleteById(id);
    }
}