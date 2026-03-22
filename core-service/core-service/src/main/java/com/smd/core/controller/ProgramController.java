package com.smd.core.controller;

import com.smd.core.dto.DepartmentSimpleDto;
import com.smd.core.dto.ProgramRequest;
import com.smd.core.dto.ProgramResponse;
import com.smd.core.entity.Department;
import com.smd.core.entity.Program;
import com.smd.core.service.ProgramService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;

    @GetMapping
    public ResponseEntity<List<ProgramResponse>> getAllPrograms() {
        List<Program> programs = programService.getAllPrograms();
        List<ProgramResponse> response = programs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponse> getProgramById(@PathVariable Long id) {
        Program program = programService.getProgramById(id);
        return ResponseEntity.ok(convertToDto(program));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<ProgramResponse>> getProgramsByDepartmentId(@PathVariable Long departmentId) {
        List<Program> programs = programService.getProgramsByDepartmentId(departmentId);
        List<ProgramResponse> response = programs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // @PostMapping
    // public ResponseEntity<ProgramResponse> createProgram(@RequestBody Program program) {
    //     Program created = programService.createProgram(program);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    // }
    @PostMapping
    @Operation(summary = "Create a new program")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Program created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Department not found")
    })
    public ResponseEntity<ProgramResponse> createProgram(@Valid @RequestBody ProgramRequest request) {
        // Convert DTO to Entity
        Program program = Program.builder()
            .programName(request.getProgramName())
            .department(Department.builder()
                .departmentId(request.getDepartment().getDepartmentId())
                .build())
            .build();
        
        Program created = programService.createProgram(program);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<ProgramResponse> updateProgram(@PathVariable Long id, @RequestBody Program program) {
    //     Program updated = programService.updateProgram(id, program);
    //     return ResponseEntity.ok(convertToDto(updated));
    // }
    @PutMapping("/{id}")
    @Operation(summary = "Update a program")
    public ResponseEntity<ProgramResponse> updateProgram(
        @PathVariable Long id, 
        @Valid @RequestBody ProgramRequest request) {
        
        Program program = Program.builder()
            .programName(request.getProgramName())
            .department(Department.builder()
                .departmentId(request.getDepartment().getDepartmentId())
                .build())
            .build();
        
        Program updated = programService.updateProgram(id, program);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }

    private ProgramResponse convertToDto(Program program) {
        return ProgramResponse.builder()
            .programId(program.getProgramId())
            .programName(program.getProgramName())
            .department(program.getDepartment() != null ?
                DepartmentSimpleDto.builder()
                    .departmentId(program.getDepartment().getDepartmentId())
                    .deptName(program.getDepartment().getDeptName())
                    .build() : null)
            .build();
    }
}
