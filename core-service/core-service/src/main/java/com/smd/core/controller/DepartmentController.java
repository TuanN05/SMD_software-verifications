package com.smd.core.controller;

import com.smd.core.dto.CourseSimpleDto;
import com.smd.core.dto.DepartmentRequest;
import com.smd.core.dto.DepartmentResponse;
import com.smd.core.dto.ProgramSimpleDto;
import com.smd.core.entity.Department;
import com.smd.core.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        List<DepartmentResponse> response = departments.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@Positive @PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(convertToDto(department));
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department department = new Department();
        department.setDeptName(request.getDeptName());
        Department created = departmentService.createDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@Positive @PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        Department department = new Department();
        department.setDeptName(request.getDeptName());
        Department updated = departmentService.updateDepartment(id, department);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@Positive @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    private DepartmentResponse convertToDto(Department department) {
        return DepartmentResponse.builder()
            .departmentId(department.getDepartmentId())
            .deptName(department.getDeptName())
            .courses(department.getCourses() != null ? 
                department.getCourses().stream()
                    .map(course -> CourseSimpleDto.builder()
                        .courseId(course.getCourseId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .credits(course.getCredits())
                        .build())
                    .collect(Collectors.toList()) : List.of())
            .programs(department.getPrograms() != null ?
                department.getPrograms().stream()
                    .map(program -> ProgramSimpleDto.builder()
                        .programId(program.getProgramId())
                        .programName(program.getProgramName())
                        .build())
                    .collect(Collectors.toList()) : List.of())
            .totalCourses(department.getCourses() != null ? department.getCourses().size() : 0)
            .totalPrograms(department.getPrograms() != null ? department.getPrograms().size() : 0)
            .build();
    }
}
