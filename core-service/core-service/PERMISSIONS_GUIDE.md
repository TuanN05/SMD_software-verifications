# SMD Permission/Authorization System Implementation

## Overview

This document describes the permission and authorization requirements for the Syllabus Management and Digitalization System (SMD).

## Roles

The system defines 6 roles:

- **ADMIN**: Full system access
- **ACADEMIC_AFFAIRS**: Academic administration access
- **HEAD_OF_DEPARTMENT**: Department management access
- **LECTURER**: Teacher/lecturer access
- **STUDENT**: Student/learner access
- **PRINCIPAL**: Principal/manager access

## Entity-Level Permissions

### 1. COURSE Management

Permissions for Course-related operations:

| Role               | Create | Read | Update | Delete |
| ------------------ | ------ | ---- | ------ | ------ |
| ADMIN              | ✓      | ✓    | ✓      | ✓      |
| ACADEMIC_AFFAIRS   | ✓      | ✓    | ✓      | ✓      |
| HEAD_OF_DEPARTMENT | ✗      | ✓    | ✓\*    | ✗      |
| LECTURER           | ✗      | ✓    | ✓\*\*  | ✗      |
| STUDENT            | ✗      | ✓    | ✗      | ✗      |

**Notes:**

- \*HEAD_OF_DEPARTMENT can update courses in their department and create/update course relationships
- \*\*LECTURER can update Syllabus & CLO within their own syllabus (no Delete)

### 2. CLO (Course Learning Outcomes) Management

Permissions for CLO-related operations:

| Role               | Create | Read | Update | Delete |
| ------------------ | ------ | ---- | ------ | ------ |
| ADMIN              | ✓      | ✓    | ✓      | ✓      |
| ACADEMIC_AFFAIRS   | ✓      | ✓    | ✓      | ✓      |
| HEAD_OF_DEPARTMENT | ✓\*    | ✓    | ✓\*    | ✗      |
| LECTURER           | ✓\*\*  | ✓    | ✓\*\*  | ✗      |
| STUDENT            | ✗      | ✓    | ✗      | ✗      |

**Notes:**

- \*HEAD_OF_DEPARTMENT can only manage CLOs in their department courses
- \*\*LECTURER can only manage CLOs in their own syllabus
- ACADEMIC_AFFAIRS has full rights + PLO mapping management

### 3. COURSE RELATIONSHIPS (Prerequisites, Co-requisites, Equivalents)

Permissions for managing course relationships:

| Role               | Create | Read | Delete |
| ------------------ | ------ | ---- | ------ |
| ADMIN              | ✓      | ✓    | ✓      |
| ACADEMIC_AFFAIRS   | ✓      | ✓    | ✓      |
| HEAD_OF_DEPARTMENT | ✓\*    | ✓    | ✓\*    |
| LECTURER           | ✗      | ✓    | ✗      |
| STUDENT            | ✗      | ✓    | ✗      |

**Notes:**

- \*HEAD_OF_DEPARTMENT can only manage relationships for courses in their department

### 4. CLO-PLO MAPPING

Permissions for managing CLO-PLO mappings:

| Role               | Create | Read | Update | Delete |
| ------------------ | ------ | ---- | ------ | ------ |
| ADMIN              | ✓      | ✓    | ✓      | ✓      |
| ACADEMIC_AFFAIRS   | ✓      | ✓    | ✓      | ✓      |
| HEAD_OF_DEPARTMENT | ✗      | ✓    | ✗      | ✗      |
| LECTURER           | ✗      | ✓    | ✗      | ✗      |
| STUDENT            | ✗      | ✓    | ✗      | ✗      |

**Notes:**

- Only ADMIN and ACADEMIC_AFFAIRS can modify mappings
- All other roles have read-only access

### 5. SYLLABUS Management

Permissions for managing syllabuses:

| Role               | Create | Read | Update | Delete | Upload PDF |
| ------------------ | ------ | ---- | ------ | ------ | ---------- |
| ADMIN              | ✓      | ✓    | ✓      | ✓      | ✓\*        |
| ACADEMIC_AFFAIRS   | ✓      | ✓    | ✓      | ✓      | ✓\*        |
| HEAD_OF_DEPARTMENT | ✓      | ✓    | ✓\*    | ✗      | ✓\*        |
| LECTURER           | ✓      | ✓    | ✓\*\*  | ✗      | ✓\*\*      |
| STUDENT            | ✗      | ✓    | ✗      | ✗      | ✗          |

**Notes:**

- \*ADMIN/ACADEMIC_AFFAIRS can upload PDF for any syllabus
- \*\*LECTURER can only update/upload PDF for their own syllabus

## Implementation Details

### Security Configuration

All permissions are implemented using Spring Security `@PreAuthorize` annotations with method-level security.

### Key Authorization Annotations

#### Read Operations (No Authorization Required)

```java
@GetMapping
public ResponseEntity<List<CourseResponse>> getAllCourses() { ... }
```

#### ADMIN Only

```java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<CourseResponse> createCourse(...) { ... }
```

#### Multiple Roles

```java
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS')")
public ResponseEntity<CourseResponse> createCourse(...) { ... }
```

### Authorization Utility Class

A new `AuthorizationUtil` component provides helper methods for checking permissions at the service level:

```java
@Component
public class AuthorizationUtil {
    public String getCurrentUsername() { ... }
    public User getCurrentUser() { ... }
    public boolean hasRole(String role) { ... }
    public boolean hasAnyRole(String... roles) { ... }
    public boolean isInDepartment(Long departmentId) { ... }
    public boolean canManageCoursesInDepartment(Long departmentId) { ... }
    // ... more utility methods
}
```

## Controllers with Updated Permissions

### 1. CourseController

**File:** `src/main/java/com/smd/core/controller/CourseController.java`

- GET endpoints: All authenticated users
- POST (create): ADMIN, ACADEMIC_AFFAIRS
- PUT (update): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT
- DELETE: ADMIN, ACADEMIC_AFFAIRS

### 2. CLOController

**File:** `src/main/java/com/smd/core/controller/CLOController.java`

- GET endpoints: All authenticated users
- POST (create): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT, LECTURER
- PUT (update): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT, LECTURER
- DELETE: ADMIN, ACADEMIC_AFFAIRS

### 3. CourseRelationController

**File:** `src/main/java/com/smd/core/controller/CourseRelationController.java`

- GET endpoints: All authenticated users
- POST (create): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT
- DELETE: ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT

### 4. CLOPLOMappingController

**File:** `src/main/java/com/smd/core/controller/CLOPLOMappingController.java`

- GET endpoints: All authenticated users
- POST (create/batch): ADMIN, ACADEMIC_AFFAIRS
- PUT (update): ADMIN, ACADEMIC_AFFAIRS
- DELETE (all variants): ADMIN, ACADEMIC_AFFAIRS

### 5. SyllabusController

**File:** `src/main/java/com/smd/core/controller/SyllabusController.java`

- GET endpoints: All authenticated users
- POST (create): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT, LECTURER
- PUT (update): ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT, LECTURER
- DELETE: ADMIN, ACADEMIC_AFFAIRS, HEAD_OF_DEPARTMENT
- Upload PDF: All authorized users (lecturer can upload for own syllabus)

## Department-Level Restrictions

For HEAD_OF_DEPARTMENT role, the following restrictions apply:

- Can only manage entities belonging to their department
- When creating/updating course-related entities, must validate department membership
- Cannot access entities from other departments

### Recommended Service-Level Validation

Services should implement additional checks for department-level permissions:

```java
public class CourseService {
    @Autowired
    private AuthorizationUtil authorizationUtil;

    public Course updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(...);

        // For HEAD_OF_DEPARTMENT, verify they belong to the course's department
        if (authorizationUtil.isHeadOfDepartment()) {
            if (!authorizationUtil.isInDepartment(course.getDepartment().getDepartmentId())) {
                throw new AccessDeniedException("You can only manage courses in your department");
            }
        }

        // ... update logic
    }
}
```

## Best Practices

1. **Always check authentication**: Use `@PreAuthorize` for all modification endpoints
2. **Read operations**: Allow all authenticated users unless there's a specific business reason to restrict
3. **Fail fast**: Check permissions early in the request processing
4. **Service-level validation**: Implement additional checks in services for complex permission rules
5. **Log access denials**: Track unauthorized access attempts for security monitoring
6. **Document permissions**: Keep permissions documented and up-to-date

## Testing

For each endpoint, create tests covering:

1. Authorized access (happy path)
2. Unauthorized access (permission denied)
3. Unauthenticated access (no token/invalid token)
4. Department-level restrictions (for applicable roles)

## Future Enhancements

1. **Custom permission annotations**: Create `@RequiresDepartmentAccess`, `@RequiresLecturerOwnership`
2. **Permission caching**: Cache role/department checks for performance
3. **Audit logging**: Log all permission checks and access denials
4. **Dynamic permissions**: Allow administrators to customize permissions via database
5. **Row-level security**: Implement RLS for more granular control
