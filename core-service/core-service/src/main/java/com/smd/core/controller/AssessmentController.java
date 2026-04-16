package com.smd.core.controller;

import com.smd.core.dto.AssessmentRequest;
import com.smd.core.dto.AssessmentResponse;
import com.smd.core.entity.Assessment;
import com.smd.core.service.AssessmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@Validated
public class AssessmentController {
    private final AssessmentService assessmentService;

    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> getAllAssessments() {
        List<Assessment> assessments = assessmentService.getAllAssessments();
        List<AssessmentResponse> response = assessments.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponse> getAssessmentById(@PathVariable @Positive Long id) {
        Assessment assessment = assessmentService.getAssessmentById(id);
        return ResponseEntity.ok(convertToDto(assessment));
    }

    @GetMapping("/syllabus/{syllabusId}")
    public ResponseEntity<List<AssessmentResponse>> getAssessmentsBySyllabusId(@PathVariable @Positive Long syllabusId) {
        List<Assessment> assessments = assessmentService.getAssessmentsBySyllabusId(syllabusId);
        List<AssessmentResponse> response = assessments.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AssessmentResponse> createAssessment(@Valid @RequestBody AssessmentRequest request) {
        Assessment created = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponse> updateAssessment(@PathVariable @Positive Long id, @Valid @RequestBody AssessmentRequest request) {
        Assessment updated = assessmentService.updateAssessment(id, request);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable @Positive Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }

    private AssessmentResponse convertToDto(Assessment assessment) {
        return AssessmentResponse.builder()
            .assessmentId(assessment.getAssessmentId())
            .syllabusId(assessment.getSyllabus() != null ? assessment.getSyllabus().getSyllabusId() : null)
            .name(assessment.getName())
            .weightPercent(assessment.getWeightPercent())
            .criteria(assessment.getCriteria())
            .build();
    }
}
