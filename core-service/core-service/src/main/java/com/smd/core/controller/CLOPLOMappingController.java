package com.smd.core.controller;

import com.smd.core.dto.CLOPLOMappingResponse;
import com.smd.core.service.CLOPLOMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clo-plo-mappings")
@RequiredArgsConstructor
@Tag(name = "CLO-PLO Mapping", description = "APIs for managing CLO-PLO mappings")
public class CLOPLOMappingController {
    
    private final CLOPLOMappingService mappingService;
    
    @GetMapping
    @Operation(summary = "Get all CLO-PLO mappings")
    public ResponseEntity<List<CLOPLOMappingResponse>> getAllMappings() {
        return ResponseEntity.ok(mappingService.getAllMappings());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get CLO-PLO mapping by ID")
    public ResponseEntity<CLOPLOMappingResponse> getMappingById(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.getMappingById(id));
    }
    
    @GetMapping("/clo/{cloId}")
    @Operation(summary = "Get all PLO mappings for a specific CLO")
    public ResponseEntity<List<CLOPLOMappingResponse>> getMappingsByCloId(@PathVariable Long cloId) {
        return ResponseEntity.ok(mappingService.getMappingsByCloId(cloId));
    }
    
    @GetMapping("/plo/{ploId}")
    @Operation(summary = "Get all CLO mappings for a specific PLO")
    public ResponseEntity<List<CLOPLOMappingResponse>> getMappingsByPloId(@PathVariable Long ploId) {
        return ResponseEntity.ok(mappingService.getMappingsByPloId(ploId));
    }
    
    @GetMapping("/syllabus/{syllabusId}")
    @Operation(summary = "Get all CLO-PLO mappings for a syllabus")
    public ResponseEntity<List<CLOPLOMappingResponse>> getMappingsBySyllabusId(@PathVariable Long syllabusId) {
        return ResponseEntity.ok(mappingService.getMappingsBySyllabusId(syllabusId));
    }
    
    @GetMapping("/program/{programId}")
    @Operation(summary = "Get all CLO-PLO mappings for a program")
    public ResponseEntity<List<CLOPLOMappingResponse>> getMappingsByProgramId(@PathVariable Long programId) {
        return ResponseEntity.ok(mappingService.getMappingsByProgramId(programId));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Create a new CLO-PLO mapping")
    public ResponseEntity<CLOPLOMappingResponse> createMapping(
            @RequestBody Map<String, Object> request) {
        Long cloId = Long.valueOf(request.get("cloId").toString());
        Long ploId = Long.valueOf(request.get("ploId").toString());
        String mappingLevel = request.get("mappingLevel").toString();
        
        CLOPLOMappingResponse created = mappingService.createMapping(cloId, ploId, mappingLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Create multiple mappings for a CLO")
    public ResponseEntity<List<CLOPLOMappingResponse>> createMappingsForCLO(
            @RequestBody Map<String, Object> request) {
        Long cloId = Long.valueOf(request.get("cloId").toString());
        @SuppressWarnings("unchecked")
        List<Number> ploIdNumbers = (List<Number>) request.get("ploIds");
        List<Long> ploIds = ploIdNumbers.stream()
                .map(Number::longValue)
                .toList();
        String mappingLevel = request.get("mappingLevel").toString();
        
        List<CLOPLOMappingResponse> created = mappingService.createMappingsForCLO(cloId, ploIds, mappingLevel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Update mapping level")
    public ResponseEntity<CLOPLOMappingResponse> updateMapping(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String mappingLevel = request.get("mappingLevel");
        return ResponseEntity.ok(mappingService.updateMapping(id, mappingLevel));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Delete a CLO-PLO mapping")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long id) {
        mappingService.deleteMapping(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/clo/{cloId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Delete all mappings for a CLO")
    public ResponseEntity<Void> deleteMappingsByCloId(@PathVariable Long cloId) {
        mappingService.deleteMappingsByCloId(cloId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/plo/{ploId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    @Operation(summary = "Delete all mappings for a PLO")
    public ResponseEntity<Void> deleteMappingsByPloId(@PathVariable Long ploId) {
        mappingService.deleteMappingsByPloId(ploId);
        return ResponseEntity.noContent().build();
    }
}
