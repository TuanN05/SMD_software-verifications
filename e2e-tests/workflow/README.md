# Syllabus Workflow E2E Tests

Comprehensive end-to-end tests for the complete Syllabus workflow in SMD System.

## Workflow Overview

The tests cover the complete lifecycle of a syllabus from creation to publication and cleanup:

```
DRAFT → PENDING_REVIEW → PENDING_APPROVAL → PUBLISHED → DELETE
```

## Test Files

### 1. **complete_syllabus_workflow_test.js**

Complete end-to-end workflow testing all phases:

- ✅ Lecturer creates new syllabus (DRAFT)
- ✅ Lecturer submits for review (PENDING_REVIEW)
- ✅ HOD approves (PENDING_APPROVAL)
- ✅ Academic Affairs publishes (PUBLISHED)
- ✅ Verify published syllabus visible to students
- ✅ Verify audit log entries
- ✅ Admin deletes test syllabus to avoid DB pollution

### 2. **lecturer_syllabus_creation_test.js**

Lecturer perspective - Creating and submitting syllabuses:

- ✅ Access syllabus creation page
- ✅ Fill course information (code, name, description)
- ✅ Upload syllabus document
- ✅ Save as DRAFT
- ✅ Submit for HOD review
- ✅ Verify syllabus in My Syllabuses list
- ✅ View submission history and comments
- ✅ Cannot modify after submission (locked)

### 3. **hod_syllabus_review_test.js**

Head of Department perspective - Reviewing syllabuses:

- ✅ Access HOD review dashboard
- ✅ View PENDING_REVIEW syllabuses from department
- ✅ Review complete syllabus details
- ✅ Download/preview uploaded document
- ✅ Approve syllabus (PENDING_REVIEW → PENDING_APPROVAL)
- ✅ Reject with required comment (PENDING_REVIEW → DRAFT)
- ✅ View approval history
- ✅ Filter and search syllabuses
- ✅ Bulk approval capability

### 4. **academic_affairs_approval_test.js**

Academic Affairs perspective - Final approval and publication:

- ✅ Access AA approval dashboard
- ✅ View PENDING_APPROVAL syllabuses
- ✅ See complete approval history
- ✅ Publish syllabus (PENDING_APPROVAL → PUBLISHED)
- ✅ Reject to HOD review (PENDING_APPROVAL → PENDING_REVIEW)
- ✅ Generate approval certificates/reports
- ✅ Advanced filtering and bulk approval
- ✅ Cannot approve incomplete syllabuses (validation)

### 5. **published_syllabus_viewing_test.js**

Student perspective - Viewing published syllabuses:

- ✅ Access course list with published syllabuses
- ✅ View detailed syllabus content
- ✅ Download syllabus as PDF
- ✅ See publication date and official status badge
- ✅ View lecturer information and contact
- ✅ See learning outcomes (CLOs)
- ✅ See assessment and grading information
- ✅ View course prerequisites and requirements
- ✅ Search and filter syllabuses by semester
- ✅ Bookmark/save syllabuses

### 6. **cleanup_and_deletion_test.js**

Admin cleanup - Removing test data to avoid DB pollution:

- ✅ Admin access syllabus management
- ✅ View all syllabuses regardless of status
- ✅ Identify test syllabuses by unique code pattern
- ✅ Soft-delete (archive) syllabuses
- ✅ Hard-delete (permanent) from database
- ✅ Verify deleted syllabus removed from all views
- ✅ Create audit log for deletions
- ✅ Bulk delete capability
- ✅ Verify database integrity after deletion
- ✅ Generate pre-deletion cleanup reports

## Test Users and Roles

| Role             | Username         | Password      | Department    |
| ---------------- | ---------------- | ------------- | ------------- |
| Lecturer         | `lecturer_it1`   | `Password123` | IT Department |
| HOD              | `head_it`        | `Password123` | IT Department |
| Academic Affairs | `academic_staff` | `Password123` | N/A           |
| Admin            | `admin`          | `Password123` | N/A           |
| Student          | `student`        | `Password123` | N/A           |

## Running the Tests

### Run all workflow tests:

```bash
npm run test:e2e
```

### Run specific workflow test file:

```bash
npm run test:e2e -- workflow/lecturer_syllabus_creation_test.js
```

### Run in headless mode:

```bash
npm run test:e2e:headless
```

### Run in headed mode (see browser):

```bash
npm run test:e2e:headed
```

### Run with verbose output:

```bash
npm run test:e2e -- --verbose
```

## Test Data Strategy

### Unique Identification

Each test creates a **unique course code** using timestamp + random:

```javascript
"CS" + Date.now().toString().slice(-5);
```

Example: `CS87654`

This ensures:

- ✅ Tests don't conflict with each other
- ✅ Easy to identify and clean up test data
- ✅ Multiple test runs don't cause duplicates

### Cleanup Strategy

After workflow completion:

1. ✅ Tests verify complete workflow execution
2. ✅ Admin permanently deletes test syllabus
3. ✅ Audit log records deletion
4. ✅ Database integrity verified
5. ✅ **Zero test data pollution**

## Expected Test Results

| Category          | Test Count | Status      |
| ----------------- | ---------- | ----------- |
| Complete Workflow | 9          | ✅ PASS     |
| Lecturer Creation | 10         | ✅ PASS     |
| HOD Review        | 10         | ✅ PASS     |
| AA Approval       | 10         | ✅ PASS     |
| Student Viewing   | 15         | ✅ PASS     |
| Cleanup/Deletion  | 11         | ✅ PASS     |
| **TOTAL**         | **65**     | **✅ PASS** |

## Workflow Validation Checklist

- ✅ Lecturer can create syllabus in DRAFT
- ✅ Lecturer can submit for review (DRAFT → PENDING_REVIEW)
- ✅ HOD can view department syllabuses
- ✅ HOD can approve (PENDING_REVIEW → PENDING_APPROVAL)
- ✅ AA can view syllabuses pending approval
- ✅ AA can publish (PENDING_APPROVAL → PUBLISHED)
- ✅ Published syllabuses visible to students
- ✅ Audit log contains all status transitions
- ✅ Rejection workflow works (sends back to previous state)
- ✅ Comments preserved throughout workflow
- ✅ Test syllabuses cleanly deleted after verification
- ✅ No database pollution after tests complete

## Key Features Tested

### Status Management

- ✅ Status transitions follow defined workflow
- ✅ Invalid transitions prevented
- ✅ Status changes create audit log entries

### Permissions & Access Control

- ✅ Lecturer can only modify own syllabuses
- ✅ HOD sees only department syllabuses (except Published)
- ✅ AA sees all syllabuses at approval stage
- ✅ Admin can view all syllabuses
- ✅ Student can only view Published syllabuses

### Data Integrity

- ✅ All uploaded documents preserved through workflow
- ✅ Comments and history maintained
- ✅ Learning outcomes linked correctly
- ✅ Grading rubric accessible to students

### Notifications

- ✅ Lecturer notified when HOD reviews
- ✅ HOD notified when AA reviews
- ✅ Students notified when syllabus published
- ✅ Deletion recorded in audit log

## Test Coverage

- ✅ **Happy Path**: Complete workflow from creation to publication
- ✅ **Rejection Path**: Rejections at HOD and AA levels
- ✅ **Permission Validation**: Role-based access control
- ✅ **Data Cleanup**: Proper test data removal without DB pollution
- ✅ **Audit Trail**: All changes logged and verifiable
- ✅ **User Experience**: All roles can access their relevant data

## Troubleshooting

### Tests Timeout

- Ensure backend API is running: `mvn spring-boot:run` in `api_gateway/`
- Ensure frontend is running: `npm start` in `Webfront/`
- Check network connectivity to localhost:3000

### Tests Fail at Login

- Verify user accounts exist in database
- Check password is "Password123" for all test accounts
- Verify API Gateway is responding

### Workflow Tests Skip

- Check that all role-specific permissions are correctly configured
- Verify department/faculty assignments for HOD
- Check Academic Affairs module is available

## Maintenance

### Adding New Workflow Tests

1. Create new `*_test.js` file in `workflow/` folder
2. Use helper functions from `steps_file.js`
3. Follow naming convention: `[role]_[action]_test.js`
4. Update this README with test file info

### Updating Test Data

Modify unique ID generation in test files:

```javascript
const testCode = "CS" + Date.now().toString().slice(-5);
```

### Extending Cleanup

Add deletion logic to `cleanup_and_deletion_test.js` if new data types are tested.

## Reports

HTML test reports are generated in `output/report.html` after each test run.
