package com.smd.core.controller;

import com.smd.core.dto.CourseResponse;
import com.smd.core.dto.CourseRequest;
import com.smd.core.dto.DepartmentSimpleDto;
import com.smd.core.entity.Course;
import com.smd.core.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.smd.core.dto.CourseRelationResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        List<CourseResponse> response = courses.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(convertToDto(course));
    }

    @GetMapping("/code/{courseCode}")
    public ResponseEntity<CourseResponse> getCourseByCourseCode(@PathVariable String courseCode) {
        Course course = courseService.getCourseByCourseCode(courseCode);
        return ResponseEntity.ok(convertToDto(course));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        Course created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS', 'HEAD_OF_DEPARTMENT')")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        Course updated = courseService.updateCourse(id, request);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    private CourseResponse convertToDto(Course course) {
        return CourseResponse.builder()
            .courseId(course.getCourseId())
            .courseCode(course.getCourseCode())
            .courseName(course.getCourseName())
            .credits(course.getCredits())
            .department(course.getDepartment() != null ? 
                DepartmentSimpleDto.builder()
                    .departmentId(course.getDepartment().getDepartmentId())
                    .deptName(course.getDepartment().getDeptName())
                    .build() : null)
            .courseType(course.getCourseType() != null ? course.getCourseType().name() : null)
            .build();
    }

    @GetMapping("/{id}/relations")
    public ResponseEntity<List<CourseRelationResponse>> getCourseRelations(@PathVariable Long id) {
        List<CourseRelationResponse> relations = courseService.getCourseRelations(id);
        return ResponseEntity.ok(relations);
    }
}
