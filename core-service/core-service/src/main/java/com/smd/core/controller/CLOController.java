package com.smd.core.controller;

import com.smd.core.dto.CLORequest;
import com.smd.core.dto.CLOResponse;
import com.smd.core.entity.CLO;
import com.smd.core.service.CLOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clos")
@RequiredArgsConstructor
public class CLOController {
    private final CLOService cloService;

    @GetMapping
    public ResponseEntity<List<CLOResponse>> getAllCLOs() {
        List<CLO> clos = cloService.getAllCLOs();
        List<CLOResponse> response = clos.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CLOResponse> getCLOById(@PathVariable Long id) {
        CLO clo = cloService.getCLOById(id);
        return ResponseEntity.ok(convertToDto(clo));
    }
    
    @GetMapping("/{id}/with-mappings")
    public ResponseEntity<CLOResponse> getCLOWithMappings(@PathVariable Long id) {
        CLOResponse response = cloService.getCLOWithMappings(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/syllabus/{syllabusId}")
    public ResponseEntity<List<CLOResponse>> getCLOsBySyllabusId(@PathVariable Long syllabusId) {
        List<CLO> clos = cloService.getCLOsBySyllabusId(syllabusId);
        List<CLOResponse> response = clos.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CLOResponse> createCLO(@Valid @RequestBody CLORequest request) {
        CLO created = cloService.createCLO(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CLOResponse> updateCLO(@PathVariable Long id, @Valid @RequestBody CLORequest request) {
        CLO updated = cloService.updateCLO(id, request);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCLO(@PathVariable Long id) {
        cloService.deleteCLO(id);
        return ResponseEntity.noContent().build();
    }

    private CLOResponse convertToDto(CLO clo) {
        return CLOResponse.builder()
            .cloId(clo.getCloId())
            .syllabusId(clo.getSyllabus() != null ? clo.getSyllabus().getSyllabusId() : null)
            .cloCode(clo.getCloCode())
            .cloDescription(clo.getCloDescription())
            .build();
    }
}
