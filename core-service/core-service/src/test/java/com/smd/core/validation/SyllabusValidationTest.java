package com.smd.core.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.smd.core.dto.CreateSyllabusRequest;
import com.smd.core.dto.CreateVersionRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("Syllabus Request Validation Tests")
class SyllabusValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== CreateSyllabusRequest Tests ====================

    @Test
    @DisplayName("Should pass validation with valid CreateSyllabusRequest")
    void testValidCreateSyllabusRequest() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("Initial version")
                .description("Test description")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Should have no violations");
    }

    @Test
    @DisplayName("Should fail when courseId is null")
    void testMissingCourseId() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Course ID is required")));
    }

    @Test
    @DisplayName("Should fail when lecturerId is null")
    void testMissingLecturerId() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .academicYear("2024-2025")
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Lecturer ID is required")));
    }

    @Test
    @DisplayName("Should fail when academicYear is null")
    void testMissingAcademicYear() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Academic year is required")));
    }

    @Test
    @DisplayName("Should fail with invalid academicYear format (no hyphen)")
    void testInvalidAcademicYearFormat_NoHyphen() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("20242025")
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Academic year must be in format YYYY-YYYY")));
    }

    @Test
    @DisplayName("Should fail with invalid academicYear format (short year)")
    void testInvalidAcademicYearFormat_ShortYear() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("24-25")
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Academic year must be in format YYYY-YYYY")));
    }

    @Test
    @DisplayName("Should pass with valid academicYear format (2024-2025)")
    void testValidAcademicYearFormat() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().noneMatch(v -> v.getMessage().contains("Academic year must be in format")));
    }

    @Test
    @DisplayName("Should fail when versionNotes is blank")
    void testBlankVersionNotes() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version notes cannot be empty")));
    }

    @Test
    @DisplayName("Should fail when versionNotes is null")
    void testNullVersionNotes() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes(null)
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version notes cannot be empty")));
    }

    @Test
    @DisplayName("Should fail when versionNotes exceeds 1000 characters")
    void testVersionNotesTooLong() {
        String longNotes = "a".repeat(1001);
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes(longNotes)
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Version notes must be between 1 and 1000 characters")));
    }

    @Test
    @DisplayName("Should pass with versionNotes of exactly 1000 characters")
    void testVersionNotesExactly1000Chars() {
        String notes = "a".repeat(1000);
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes(notes)
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getMessage().contains("Version notes must be between")));
    }

    @Test
    @DisplayName("Should fail when description exceeds 5000 characters")
    void testDescriptionTooLong() {
        String longDesc = "a".repeat(5001);
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("Test")
                .description(longDesc)
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Description must not exceed 5000 characters")));
    }

    @Test
    @DisplayName("Should allow empty description")
    void testEmptyDescription() {
        CreateSyllabusRequest request = CreateSyllabusRequest.builder()
                .courseId(1L)
                .lecturerId(2L)
                .academicYear("2024-2025")
                .versionNotes("Test")
                .description("")
                .build();

        Set<ConstraintViolation<CreateSyllabusRequest>> violations = validator.validate(request);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getMessage().contains("Description must not exceed")));
    }

    // ==================== CreateVersionRequest Tests ====================

    @Test
    @DisplayName("Should pass validation with valid CreateVersionRequest")
    void testValidCreateVersionRequest() {
        CreateVersionRequest request = CreateVersionRequest.builder()
                .sourceSyllabusId(1L)
                .versionNotes("Updated version")
                .copyMaterials(true)
                .copySessionPlans(true)
                .copyAssessments(true)
                .copyCLOs(true)
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size(), "Should have no violations");
    }

    @Test
    @DisplayName("Should fail when sourceSyllabusId is null")
    void testMissingSourceSyllabusId() {
        CreateVersionRequest request = CreateVersionRequest.builder()
                .versionNotes("Test")
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Source syllabus ID is required")));
    }

    @Test
    @DisplayName("Should fail when versionNotes is blank in CreateVersionRequest")
    void testBlankVersionNotesInCreateVersion() {
        CreateVersionRequest request = CreateVersionRequest.builder()
                .sourceSyllabusId(1L)
                .versionNotes("")
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version notes cannot be empty")));
    }

    @Test
    @DisplayName("Should fail when versionNotes is null in CreateVersionRequest")
    void testNullVersionNotesInCreateVersion() {
        CreateVersionRequest request = CreateVersionRequest.builder()
                .sourceSyllabusId(1L)
                .versionNotes(null)
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Version notes cannot be empty")));
    }

    @Test
    @DisplayName("Should fail when versionNotes exceeds 1000 characters in CreateVersionRequest")
    void testVersionNotesTooLongInCreateVersion() {
        String longNotes = "a".repeat(1001);
        CreateVersionRequest request = CreateVersionRequest.builder()
                .sourceSyllabusId(1L)
                .versionNotes(longNotes)
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Version notes must be between 1 and 1000 characters")));
    }

    @Test
    @DisplayName("Should allow single character versionNotes")
    void testSingleCharVersionNotes() {
        CreateVersionRequest request = CreateVersionRequest.builder()
                .sourceSyllabusId(1L)
                .versionNotes("A")
                .build();

        Set<ConstraintViolation<CreateVersionRequest>> violations = validator.validate(request);
        assertTrue(violations.stream()
                .noneMatch(v -> v.getMessage().contains("Version notes must be between")));
    }
}
