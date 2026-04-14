package com.smd.core.service;

import com.smd.core.entity.Course;
import com.smd.core.exception.DuplicateResourceException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.CourseRepository;
import com.smd.core.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smd.core.dto.CourseRelationResponse;
import com.smd.core.dto.CourseResponse;
import com.smd.core.dto.CourseRequest;
import com.smd.core.dto.DepartmentSimpleDto;
import com.smd.core.entity.CourseRelation;
import com.smd.core.entity.Department;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + courseCode));
    }

    @Transactional
    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("Course already exists with code: " + request.getCourseCode());
        }
        
        // Validate and fetch department
        Department department = departmentRepository.findById(request.getDepartment().getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartment().getDepartmentId()));
        
        // Create course from request
        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credits(request.getCredits())
                .courseType(request.getCourseType() != null ? 
                    Course.CourseType.valueOf(request.getCourseType()) : 
                    Course.CourseType.BAT_BUOC)
                .department(department)
                .build();
        
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, CourseRequest request) {
        Course course = getCourseById(id);
        
        // Check if course code is being changed and if the new code already exists
        if (!course.getCourseCode().equals(request.getCourseCode()) &&
                courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("Course already exists with code: " + request.getCourseCode());
        }
        
        // Validate and fetch department
        Department department = departmentRepository.findById(request.getDepartment().getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartment().getDepartmentId()));
        
        // Update course fields
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredits(request.getCredits());
        course.setCourseType(request.getCourseType() != null ? 
            Course.CourseType.valueOf(request.getCourseType()) : 
            Course.CourseType.BAT_BUOC);
        course.setDepartment(department);
        
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CourseRelationResponse> getCourseRelations(Long courseId) {
        Course course = getCourseById(courseId); // Sử dụng hàm đã có để tìm Course và check lỗi

        // Lấy danh sách prerequisiteRelations (các quan hệ mà course này là chủ thể)
        return course.getPrerequisiteRelations().stream()
                .map(this::convertRelationToDto)
                .collect(Collectors.toList());
    }

    // Helper method để convert Entity sang DTO
    private CourseRelationResponse convertRelationToDto(CourseRelation relation) {
        return CourseRelationResponse.builder()
                .relationId(relation.getRelationId())
                .relationType(relation.getRelationType())
                .relatedCourse(convertCourseToDto(relation.getRelatedCourse()))
                .build();
    }

    // Helper method để convert Course sang DTO (nếu chưa có trong Service thì copy logic từ Controller hoặc tách ra component riêng)
    // Lưu ý: Tốt nhất là nên dùng Mapper (như MapStruct) hoặc tách logic convert ra class riêng để tái sử dụng.
    // Ở đây mình viết method private đơn giản dựa trên logic của bạn.
    private CourseResponse convertCourseToDto(Course course) {
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
                .build();
    }
}
