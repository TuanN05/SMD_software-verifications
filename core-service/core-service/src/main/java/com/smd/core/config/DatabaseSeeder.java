package com.smd.core.config;

import com.smd.core.entity.*;
import com.smd.core.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
//commment
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    // 1. Auth Repositories
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. Academic Repositories
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final PLORepository ploRepository;
    private final CourseRelationRepository courseRelationRepository;

    // 3. Syllabus Core Repositories
    private final SyllabusRepository syllabusRepository;
    private final AiTaskRepository aiTaskRepository;

    // 4. Syllabus Detail Repositories
    private final MaterialRepository materialRepository;
    private final SessionPlanRepository sessionPlanRepository;
    private final AssessmentRepository assessmentRepository;
    private final CLORepository cloRepository;

    // 5. Interaction Repositories
    private final ReviewCommentRepository reviewCommentRepository;
    private final NotificationRepository notificationRepository;
    private final CourseSubscriptionRepository courseSubscriptionRepository;

    // 6. Workflow & History Repositories (THÊM MỚI)
    private final WorkflowStepRepository workflowStepRepository;
    private final SyllabusWorkflowHistoryRepository workflowHistoryRepository;
    private final SyllabusAuditLogRepository auditLogRepository;

    // 7. System Repositories (THÊM MỚI)
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Kiểm tra xem user có username là "admin" đã tồn tại chưa
        if (checkAdminExist()) {
            log.info("Admin account already exists. Database seeding skipped.");
            return; // Lệnh này sẽ dừng toàn bộ việc khởi tạo dữ liệu phía sau
        }

        log.info("STARTING DATABASE SEEDING...");

        // --- BƯỚC 1: CẤU HÌNH CƠ BẢN ---
        initRoles();
        initDepartmentsAndPrograms();
        initUsers();

        // --- BƯỚC 2: DỮ LIỆU ĐÀO TẠO ---
        initPLOs();
        initCourses();
        initCourseRelations();

        // --- BƯỚC 3: SYLLABUS & CHI TIẾT ---
        initSyllabi();
        // initSyllabusDetails(); // Gọi bên trong initSyllabi để tiện quản lý

        // --- BƯỚC 4: WORKFLOW & AUDIT (MỚI) ---
        initWorkflowSteps();      // Cần có Step trước khi tạo History
        initWorkflowHistory();    // Tạo lịch sử duyệt
        initAuditLogs();          // Tạo nhật ký hệ thống

        // --- BƯỚC 4: TƯƠNG TÁC ---
        initInteractions();
        initAiTasks();
        initCourseSubscriptions();
        initReviewComments();
        initReports();

        log.info("DATABASE SEEDING SUCCESS.");
    }

    private boolean checkAdminExist() {
        // Cách 1: Kiểm tra theo username cụ thể (Khuyên dùng)
        Optional<User> adminUser = userRepository.findByUsername("admin");
        return adminUser.isPresent();

        // Cách 2: Nếu bạn muốn kiểm tra xem có BẤT KỲ admin nào không (dựa vào Role)
        // return userRepository.existsByUserRoles_Role_RoleName("ADMIN"); 
        // (Lưu ý: Cách 2 yêu cầu bạn phải có method existsBy... trong UserRepository)
    }

    // ==========================================
    // PHẦN 1: USER & ROLE
    // ==========================================
    private void initRoles() {
        String[] roles = {"ADMIN", "LECTURER", "HEAD_OF_DEPARTMENT", "ACADEMIC_AFFAIRS", "PRINCIPAL", "STUDENT"};
        for (String roleName : roles) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().roleName(roleName).build());
                log.info("   + Created Role: {}", roleName);
            }
        }
    }

    private void initUsers() {
        String commonPass = "Password123";
        Department itDept = getDepartment("Khoa Cong Nghe Thong Tin");
        Department bizDept = getDepartment("Khoa Quan Tri Kinh Doanh");
        Department designDept = getDepartment("Khoa Thiet Ke Do Hoa");

        // 1. Admin & Managers
        createUser("admin", "admin@smd.edu.vn", "System Administrator", commonPass, "ADMIN", null);
        createUser("academic_staff", "aa@smd.edu.vn", "Academic Affairs Manager", commonPass, "ACADEMIC_AFFAIRS", null);
        createUser("principal", "principal@smd.edu.vn", "Hieu Truong", commonPass, "PRINCIPAL", null);

        // 2. Head of Departments
        User headIT = createUser("head_it", "head.it@smd.edu.vn", "Truong Khoa IT", commonPass, "HEAD_OF_DEPARTMENT", itDept);
        User headBiz = createUser("head_biz", "head.biz@smd.edu.vn", "Truong Khoa Kinh Doanh", commonPass, "HEAD_OF_DEPARTMENT", bizDept);
        
        // 3. Lecturers
        createUser("lecturer_it1", "lecturer.it1@smd.edu.vn", "Nguyen Van A (IT)", commonPass, "LECTURER", itDept);
        createUser("lecturer_it2", "lecturer.it2@smd.edu.vn", "Tran Thi B (IT)", commonPass, "LECTURER", itDept);
        createUser("lecturer_biz", "lecturer.biz@smd.edu.vn", "Le Van C (Biz)", commonPass, "LECTURER", bizDept);
        createUser("lecturer_design", "lecturer.design@smd.edu.vn", "Pham Thi D (Design)", commonPass, "LECTURER", designDept);
        
        // 4. Student
        createUser("student", "student@smd.edu.vn", "Nguyen Sinh Vien", commonPass, "STUDENT", itDept);

        // Update Head for Department
        updateDeptHead(itDept, headIT);
        updateDeptHead(bizDept, headBiz);
    }

    private User createUser(String username, String email, String fullName, String rawPass, String roleName, Department dept) {
        if (userRepository.findByUsername(username).isPresent()) {
            return userRepository.findByUsername(username).get();
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordEncoder.encode(rawPass))
                .status(User.UserStatus.ACTIVE)
                .department(dept)
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByRoleName(roleName).orElseThrow();
        userRoleRepository.save(UserRole.builder().user(savedUser).role(role).build());
        log.info("   + Created User: {} ({})", username, roleName);
        return savedUser;
    }

    private void updateDeptHead(Department dept, User head) {
        if (dept != null && head != null) {
            dept.setHeadOfDepartment(head);
            departmentRepository.save(dept);
        }
    }

    // ==========================================
    // PHẦN 2: ACADEMIC DATA
    // ==========================================
    private void initDepartmentsAndPrograms() {
        // IT
        Department itDept = createDepartmentIfNotFound("Khoa Cong Nghe Thong Tin");
        createProgramIfNotFound("Ky thuat Phan mem", itDept);
        createProgramIfNotFound("An toan Thong tin", itDept);
        createProgramIfNotFound("He thong Thong tin", itDept);

        // Business
        Department bizDept = createDepartmentIfNotFound("Khoa Quan Tri Kinh Doanh");
        createProgramIfNotFound("Digital Marketing", bizDept);
        createProgramIfNotFound("Quan tri Kinh doanh Quoc te", bizDept);

        // Design
        Department designDept = createDepartmentIfNotFound("Khoa Thiet Ke Do Hoa");
        createProgramIfNotFound("Thiet ke Do hoa", designDept);
    }

    private Department createDepartmentIfNotFound(String deptName) {
        return departmentRepository.findAll().stream()
                .filter(d -> d.getDeptName().equals(deptName))
                .findFirst()
                .orElseGet(() -> departmentRepository.save(Department.builder().deptName(deptName).build()));
    }
    
    private Department getDepartment(String name) {
        return departmentRepository.findAll().stream()
                .filter(d -> d.getDeptName().equals(name))
                .findFirst().orElse(null);
    }

    private Program createProgramIfNotFound(String programName, Department dept) {
        return programRepository.findAll().stream()
                .filter(p -> p.getProgramName().equals(programName))
                .findFirst()
                .orElseGet(() -> programRepository.save(Program.builder().programName(programName).department(dept).build()));
    }

    private void initPLOs() {
        Program seProgram = programRepository.findAll().stream()
                .filter(p -> p.getProgramName().equals("Ky thuat Phan mem")).findFirst().orElse(null);

        if (seProgram != null && ploRepository.findByProgram_ProgramId(seProgram.getProgramId()).isEmpty()) {
            createPLO(seProgram, "PLO1", "Áp dụng kiến thức toán học, khoa học và kỹ thuật.");
            createPLO(seProgram, "PLO2", "Thiết kế và tiến hành thực nghiệm, phân tích dữ liệu.");
            createPLO(seProgram, "PLO3", "Thiết kế hệ thống, thành phần hoặc quy trình đáp ứng nhu cầu.");
            createPLO(seProgram, "PLO4", "Hoạt động hiệu quả trong các nhóm đa ngành.");
            createPLO(seProgram, "PLO5", "Nhận diện, diễn đạt và giải quyết các vấn đề kỹ thuật.");
        }
        
        Program mktProgram = programRepository.findAll().stream()
                .filter(p -> p.getProgramName().equals("Digital Marketing")).findFirst().orElse(null);
        if (mktProgram != null && ploRepository.findByProgram_ProgramId(mktProgram.getProgramId()).isEmpty()) {
             createPLO(mktProgram, "PLO1", "Hiểu biết về thị trường và hành vi khách hàng.");
             createPLO(mktProgram, "PLO2", "Lập kế hoạch Digital Marketing hiệu quả.");
        }
    }

    private void createPLO(Program program, String code, String desc) {
        ploRepository.save(PLO.builder()
                .program(program).ploCode(code).ploDescription(desc)
                .build());
    }

    private void initCourses() {
        Department itDept = getDepartment("Khoa Cong Nghe Thong Tin");
        Department bizDept = getDepartment("Khoa Quan Tri Kinh Doanh");
        Department designDept = getDepartment("Khoa Thiet Ke Do Hoa");

        // IT Courses - Creating a comprehensive CS curriculum tree
        if (itDept != null) {
            // Year 1 - Foundation Courses
            createCourse("MAT101", "Discrete Mathematics", 3, itDept);
            createCourse("PRF192", "Programming Fundamentals", 3, itDept);
            createCourse("COM101", "Introduction to Computer Science", 3, itDept);
            
            // Year 2 - Core Courses
            createCourse("PRO192", "Object-Oriented Programming", 3, itDept);
            createCourse("CSD201", "Data Structures & Algorithms", 3, itDept);
            createCourse("DBI202", "Database Systems", 3, itDept);
            createCourse("OSG202", "Operating Systems", 3, itDept);
            createCourse("NWC203", "Computer Networks", 3, itDept);
            
            // Year 3 - Advanced Courses
            createCourse("JPD113", "Java Programming", 3, itDept);
            createCourse("WED201c", "Web Design & Development", 3, itDept);
            createCourse("SWR302", "Software Requirements", 3, itDept);
            createCourse("SWT301", "Software Testing", 3, itDept);
            createCourse("SWD392", "Software Design & Architecture", 4, itDept);
            createCourse("ALG301", "Advanced Algorithms", 3, itDept);
            
            // Year 4 - Specialization Courses
            createCourse("AI401", "Artificial Intelligence", 4, itDept);
            createCourse("ML402", "Machine Learning", 4, itDept);
            createCourse("SEC403", "Information Security", 3, itDept);
            createCourse("MBD404", "Mobile Development", 3, itDept);
            createCourse("CAP490", "Capstone Project", 4, itDept);
        }

        // Business Courses
        if (bizDept != null) {
            createCourse("MKT101", "Marketing Fundamentals", 3, bizDept);
            createCourse("ECO111", "Microeconomics", 3, bizDept);
            createCourse("OBM202", "Organizational Behavior", 3, bizDept);
        }
        
        // Design Courses
        if (designDept != null) {
            createCourse("DES101", "Color Theory", 3, designDept);
            createCourse("PHO102", "Photography Basics", 2, designDept);
        }
        
        log.info("✓ Created {} courses", courseRepository.count());
    }

    /**
     * Hàm tạo môn học với đầy đủ tham số.
     */
    private void createCourse(String code, String name, int credits, Course.CourseType type, Department dept) {
        if (courseRepository.findByCourseCode(code).isPresent()) return;

        // Sử dụng giá trị mặc định nếu type bị null khi gọi hàm
        Course.CourseType finalType = (type != null) ? type : Course.CourseType.BAT_BUOC;

        courseRepository.save(Course.builder()
                .courseCode(code)
                .courseName(name)
                .credits(credits)
                .courseType(finalType)
                .department(dept)
                .build());
        
        log.info("   + Created Course: {} ({})", code, finalType);
    }

    /**
     * Hàm nạp chồng (Overloading): Mặc định loại môn học là BAT_BUOC nếu không truyền tham số type.
     */
    private void createCourse(String code, String name, int credits, Department dept) {
        createCourse(code, name, credits, Course.CourseType.BAT_BUOC, dept);
    }


    /**
     * Initialize comprehensive course relationships to create a course dependency tree
     * This creates a realistic CS curriculum with prerequisites, corequisites, and equivalents
     */
    private void initCourseRelations() {
        log.info("Initializing Course Relationships...");
        
        // ==========================================
        // YEAR 1 FOUNDATIONS → YEAR 2 CORE
        // ==========================================
        
        // Mathematics is prerequisite for many courses
        createRelation("MAT101", "CSD201", CourseRelation.RelationType.PREREQUISITE);
        createRelation("MAT101", "ALG301", CourseRelation.RelationType.PREREQUISITE);
        
        // Programming Fundamentals is the foundation
        createRelation("PRF192", "PRO192", CourseRelation.RelationType.PREREQUISITE);
        createRelation("PRF192", "CSD201", CourseRelation.RelationType.PREREQUISITE);
        createRelation("PRF192", "DBI202", CourseRelation.RelationType.PREREQUISITE);
        
        // Introduction to CS helps with Operating Systems
        createRelation("COM101", "OSG202", CourseRelation.RelationType.PREREQUISITE);
        
        // ==========================================
        // YEAR 2 CORE → YEAR 3 ADVANCED
        // ==========================================
        
        // OOP is needed for advanced programming
        createRelation("PRO192", "JPD113", CourseRelation.RelationType.PREREQUISITE);
        createRelation("PRO192", "WED201c", CourseRelation.RelationType.PREREQUISITE);
        createRelation("PRO192", "SWD392", CourseRelation.RelationType.PREREQUISITE);
        createRelation("PRO192", "MBD404", CourseRelation.RelationType.PREREQUISITE);
        
        // Data Structures is crucial for advanced courses
        createRelation("CSD201", "ALG301", CourseRelation.RelationType.PREREQUISITE);
        createRelation("CSD201", "SWD392", CourseRelation.RelationType.PREREQUISITE);
        createRelation("CSD201", "AI401", CourseRelation.RelationType.PREREQUISITE);
        
        // Database Systems needed for software projects
        createRelation("DBI202", "SWR302", CourseRelation.RelationType.PREREQUISITE);
        createRelation("DBI202", "WED201c", CourseRelation.RelationType.PREREQUISITE);
        
        // Operating Systems knowledge helps with Security
        createRelation("OSG202", "SEC403", CourseRelation.RelationType.PREREQUISITE);
        
        // Networks knowledge for Security
        createRelation("NWC203", "SEC403", CourseRelation.RelationType.PREREQUISITE);
        
        // ==========================================
        // YEAR 3 ADVANCED → YEAR 4 SPECIALIZATION
        // ==========================================
        
        // Java Programming for advanced development
        createRelation("JPD113", "MBD404", CourseRelation.RelationType.PREREQUISITE);
        
        // Software Requirements for Testing
        createRelation("SWR302", "SWT301", CourseRelation.RelationType.PREREQUISITE);
        
        // Software Design for Capstone
        createRelation("SWD392", "CAP490", CourseRelation.RelationType.PREREQUISITE);
        
        // Advanced Algorithms for AI
        createRelation("ALG301", "AI401", CourseRelation.RelationType.PREREQUISITE);
        createRelation("ALG301", "ML402", CourseRelation.RelationType.PREREQUISITE);
        
        // AI prerequisite for Machine Learning
        createRelation("AI401", "ML402", CourseRelation.RelationType.PREREQUISITE);
        
        // ==========================================
        // COREQUISITES (Study Together)
        // ==========================================
        
        // Data Structures and Algorithms should be learned together with OOP
        createRelation("CSD201", "PRO192", CourseRelation.RelationType.COREQUISITE);
        
        // Database and Web Development work well together
        createRelation("WED201c", "DBI202", CourseRelation.RelationType.COREQUISITE);
        
        // Operating Systems and Networks complement each other
        createRelation("OSG202", "NWC203", CourseRelation.RelationType.COREQUISITE);
        
        // Software Requirements and Testing go hand in hand
        createRelation("SWT301", "SWR302", CourseRelation.RelationType.COREQUISITE);
        
        // ==========================================
        // EQUIVALENTS (Alternative Courses)
        // ==========================================
        
        // Java and Web Development can substitute for advanced OOP
        createRelation("JPD113", "WED201c", CourseRelation.RelationType.EQUIVALENT);
        
        log.info("✓ Created course relationship tree with {} relationships", 
                 courseRelationRepository.count());
        log.info("  - Prerequisites: Foundation → Core → Advanced → Specialization");
        log.info("  - Corequisites: Complementary courses to study together");
        log.info("  - Equivalents: Alternative course options");
    }
    
    /**
     * Helper method to create course relationships with validation
     */
    private void createRelation(String courseCode, String relatedCourseCode, CourseRelation.RelationType type) {
        Optional<Course> courseOpt = courseRepository.findByCourseCode(courseCode);
        Optional<Course> relatedCourseOpt = courseRepository.findByCourseCode(relatedCourseCode);
        
        if (courseOpt.isEmpty()) {
            log.warn("   ⚠ Course not found: {}", courseCode);
            return;
        }
        
        if (relatedCourseOpt.isEmpty()) {
            log.warn("   ⚠ Related course not found: {}", relatedCourseCode);
            return;
        }
        
        Course course = courseOpt.get();
        Course relatedCourse = relatedCourseOpt.get();
        
        // Check if relationship already exists
        boolean exists = courseRelationRepository.existsRelationship(
            course.getCourseId(), 
            relatedCourse.getCourseId(), 
            type
        );
        
        if (exists) {
            log.debug("   → Relationship already exists: {} -[{}]-> {}", 
                     courseCode, type, relatedCourseCode);
            return;
        }
        
        // Create new relationship
        CourseRelation relation = CourseRelation.builder()
                .course(course)
                .relatedCourse(relatedCourse)
                .relationType(type)
                .build();
        
        courseRelationRepository.save(relation);
        
        String arrow = switch(type) {
            case PREREQUISITE -> "→";
            case COREQUISITE -> "↔";
            case EQUIVALENT -> "≡";
        };
        
        log.info("   + {} {} {} ({})", courseCode, arrow, relatedCourseCode, type);
    }

    // ==========================================
    // PHẦN 3: SYLLABUS & DETAILS
    // ==========================================
    private void initSyllabi() {
        User lecIT1 = userRepository.findByUsername("lecturer_it1").orElse(null);
        User lecIT2 = userRepository.findByUsername("lecturer_it2").orElse(null);
        User lecBiz = userRepository.findByUsername("lecturer_biz").orElse(null);
        User lecDesign = userRepository.findByUsername("lecturer_design").orElse(null);
        
        Program seProgram = programRepository.findAll().stream().filter(p -> p.getProgramName().contains("Phan mem")).findFirst().orElse(null);
        Program mktProgram = programRepository.findAll().stream().filter(p -> p.getProgramName().contains("Marketing")).findFirst().orElse(null);
        Program designProgram = programRepository.findAll().stream().filter(p -> p.getProgramName().contains("Thiet ke")).findFirst().orElse(null);

        if (lecIT1 == null) return;

        // 1. DRAFT Syllabi
        Syllabus s1 = createSyllabusWithDetails("JPD113", "2024-2025", 1, lecIT1, seProgram, Syllabus.SyllabusStatus.DRAFT, 
            "Môn học Java Programming cung cấp kiến thức nền tảng về lập trình hướng đối tượng với Java. Sinh viên sẽ học các khái niệm cơ bản như class, object, inheritance, polymorphism, và exception handling. Khóa học tập trung vào việc phát triển kỹ năng lập trình thực tế thông qua các bài tập và dự án nhóm.");
        createSyllabusWithDetails("PRO192", "2025-2026", 1, lecIT1, seProgram, Syllabus.SyllabusStatus.DRAFT,
            "Môn Object-Oriented Programming giúp sinh viên nắm vững các nguyên lý lập trình hướng đối tượng. Nội dung bao gồm: thiết kế class diagram, UML, design patterns cơ bản, và best practices trong OOP. Môn học yêu cầu sinh viên hoàn thành 3 assignment lớn và 1 final project.");
        
        // 2. PENDING_REVIEW (Chờ trưởng bộ môn duyệt)
        createSyllabusWithDetails("DBI202", "2024-2025", 1, lecIT2, seProgram, Syllabus.SyllabusStatus.PENDING_REVIEW,
            "Database Systems là môn học thiết yếu trong chương trình đào tạo Kỹ thuật Phần mềm. Sinh viên sẽ được học về thiết kế cơ sở dữ liệu quan hệ, SQL, normalization, transaction processing, và indexing. Phần thực hành tập trung vào MySQL và PostgreSQL với các case study thực tế từ doanh nghiệp.");
        
        // 3. APPROVED (Đã được duyệt, chờ xuất bản)
        createSyllabusWithDetails("MKT101", "2024-2025", 1, lecBiz, mktProgram, Syllabus.SyllabusStatus.APPROVED,
            "Marketing Fundamentals giới thiệu các khái niệm cơ bản về marketing trong kỷ nguyên số. Nội dung gồm: phân tích thị trường, hành vi người tiêu dùng, chiến lược marketing mix (4P), và digital marketing trends. Sinh viên sẽ thực hiện các case study về các công ty hàng đầu Việt Nam và quốc tế.");
        
        // 4. PUBLISHED (Đã công khai)
        Syllabus sWeb = createSyllabusWithDetails("WED201c", "2024-2025", 2, lecIT1, seProgram, Syllabus.SyllabusStatus.PUBLISHED,
            "Web Design & Development trang bị cho sinh viên kỹ năng thiết kế và phát triển website hiện đại. Chương trình bao gồm HTML5, CSS3, JavaScript, responsive design, và các framework như React hoặc Vue.js. Sinh viên sẽ hoàn thành một dự án web application hoàn chỉnh trong nhóm 3-4 người, áp dụng Agile methodology.");
        createSyllabusWithDetails("DES101", "2024-2025", 1, lecDesign, designProgram, Syllabus.SyllabusStatus.PUBLISHED,
            "Color Theory là môn học nền tảng về lý thuyết màu sắc trong thiết kế. Sinh viên sẽ tìm hiểu về color wheel, color harmony, psychology of colors, và cách áp dụng màu sắc hiệu quả trong các dự án thiết kế đồ họa. Khóa học kết hợp lý thuyết và thực hành trên Adobe Photoshop, Illustrator.");
        
        // 5. ARCHIVED (Syllabus cũ)
        createSyllabusWithDetails("WED201c", "2023-2024", 1, lecIT1, seProgram, Syllabus.SyllabusStatus.ARCHIVED,
            "[Phiên bản cũ] Web Design & Development - phiên bản này tập trung vào HTML, CSS, và jQuery. Đã được thay thế bởi phiên bản mới với nội dung cập nhật về React và modern JavaScript frameworks.");
    }

    private Syllabus createSyllabusWithDetails(String courseCode, String year, int ver, User lecturer, Program program, Syllabus.SyllabusStatus status, String description) {
        Course course = courseRepository.findByCourseCode(courseCode).orElse(null);
        if (course == null || lecturer == null) return null;

        if (syllabusRepository.existsByCourse_CourseIdAndAcademicYearAndVersionNo(course.getCourseId(), year, ver)) {
            return syllabusRepository.findByCourse_CourseIdAndAcademicYearAndVersionNo(course.getCourseId(), year, ver).orElse(null);
        }

        boolean isLatest = (status != Syllabus.SyllabusStatus.ARCHIVED);

        Syllabus syllabus = Syllabus.builder()
                .course(course).lecturer(lecturer).program(program)
                .academicYear(year).versionNo(ver)
                .currentStatus(status)
                .description(description)
                .isLatestVersion(isLatest)
                .createdAt(LocalDateTime.now().minusDays(new Random().nextInt(30)))
                .updatedAt(LocalDateTime.now())
                .build();
        
        if (status == Syllabus.SyllabusStatus.PUBLISHED) {
            syllabus.setPublishedAt(LocalDateTime.now());
        }
        
        Syllabus saved = syllabusRepository.save(syllabus);
        log.info("   + Created Syllabus: {} v{} [{}]", courseCode, ver, status);
        
        // Add Details automatically
        addMaterials(saved);
        addSessionPlans(saved);
        addAssessments(saved);
        addCLOs(saved);
        
        return saved;
    }

    private void addMaterials(Syllabus s) {
        if (s.getCourse().getCourseCode().startsWith("JPD")) {
            materialRepository.save(Material.builder().syllabus(s).title("Core Java Volume I").author("Cay S. Horstmann").materialType(Material.MaterialType.TEXTBOOK).build());
        } else if (s.getCourse().getCourseCode().startsWith("MKT")) {
            materialRepository.save(Material.builder().syllabus(s).title("Marketing Management").author("Philip Kotler").materialType(Material.MaterialType.TEXTBOOK).build());
        } else {
             materialRepository.save(Material.builder().syllabus(s).title("General Resource for " + s.getCourse().getCourseCode()).author("Multiple Authors").materialType(Material.MaterialType.REFERENCE_BOOK).build());
        }
    }

    private void addSessionPlans(Syllabus s) {
        for (int i = 1; i <= 5; i++) {
            sessionPlanRepository.save(SessionPlan.builder()
                    .syllabus(s).weekNo(i).topic("Topic Week " + i + ": Fundamentals").teachingMethod("Lecture & Activity").build());
        }
    }

    private void addAssessments(Syllabus s) {
        assessmentRepository.save(Assessment.builder().syllabus(s).name("Quiz 1").weightPercent(10f).criteria("MCQ").build());
        assessmentRepository.save(Assessment.builder().syllabus(s).name("Midterm Exam").weightPercent(30f).criteria("Written").build());
        assessmentRepository.save(Assessment.builder().syllabus(s).name("Final Exam").weightPercent(60f).criteria("Project/Exam").build());
    }

    private void addCLOs(Syllabus s) {
        List<PLO> plos = s.getProgram() != null ? ploRepository.findByProgram_ProgramId(s.getProgram().getProgramId()) : new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            CLO clo = CLO.builder()
                    .syllabus(s)
                    .cloCode("CLO" + i)
                    .cloDescription("Student will be able to demonstrate skill level " + i + " in " + s.getCourse().getCourseName())
                    .build();
            
            // Simple mapping logic
            if (!plos.isEmpty()) {
                List<CLOPLOMapping> mappings = new ArrayList<>();
                mappings.add(CLOPLOMapping.builder().clo(clo).plo(plos.get(i % plos.size())).mappingLevel(CLOPLOMapping.MappingLevel.HIGH).build());
                clo.setPloMappings(mappings);
            }
            cloRepository.save(clo);
        }
    }

    // PHẦN 4: INTERACTION & AI
    // ==========================================
    private void initInteractions() {
        Department itDept = getDepartment("Khoa Cong Nghe Thong Tin");
        if (itDept == null || itDept.getHeadOfDepartment() == null) return;
        
        // 1. Tương tác: Trưởng bộ môn comment vào đề cương đang chờ duyệt
        List<Syllabus> pendingSyllabi = syllabusRepository.findByCurrentStatus(Syllabus.SyllabusStatus.PENDING_REVIEW);
        User head = itDept.getHeadOfDepartment();

        for (Syllabus s : pendingSyllabi) {
            if (s.getCourse().getDepartment().getDepartmentId().equals(itDept.getDepartmentId())) {
                // Tạo Comment
                reviewCommentRepository.save(ReviewComment.builder()
                        .syllabus(s)
                        .user(head)
                        .content("Đề cương cần chi tiết hơn ở phần đánh giá, vui lòng bổ sung rubrics.")
                        .createdAt(LocalDateTime.now().minusHours(2))
                        .build());
                
                // Tạo Thông báo cho Giảng viên (Lecturer)
                notificationRepository.save(Notification.builder()
                        .recipient(s.getLecturer())
                        .syllabus(s)
                        .type(Notification.NotificationType.COMMENT_ADDED)
                        .title("Yêu cầu chỉnh sửa")
                        .message("Trưởng bộ môn đã nhận xét đề cương " + s.getCourse().getCourseCode())
                        .triggeredBy(head.getUsername())
                        .isRead(false) // Chưa đọc
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }
//nigga
        // 2. Tương tác: Thông báo cho Sinh viên (Student) về đề cương đã Public
        // Giả lập student1 nhận được thông báo về môn học họ quan tâm
        User student = userRepository.findByUsername("student").orElse(null);
        // Lấy một đề cương bất kỳ (ưu tiên Published) để giả lập thông báo
        Syllabus publicSyllabus = syllabusRepository.findAll().stream()
                .filter(s -> s.getCurrentStatus() == Syllabus.SyllabusStatus.PUBLISHED)
                .findFirst()
                .orElse(!pendingSyllabi.isEmpty() ? pendingSyllabi.get(0) : null);

        if (student != null && publicSyllabus != null) {
            notificationRepository.save(Notification.builder()
                    .recipient(student)
                    .syllabus(publicSyllabus)
                    .type(Notification.NotificationType.SYLLABUS_PUBLISHED)
                    .title("Đề cương môn học mới")
                    .message("Môn học " + publicSyllabus.getCourse().getCourseName() + " (" + publicSyllabus.getCourse().getCourseCode() + ") đã cập nhật đề cương mới v" + publicSyllabus.getVersionNo())
                    .triggeredBy("admin") // Người public (thường là Principal hoặc Admin)
                    .isRead(false)
                    .createdAt(LocalDateTime.now().minusMinutes(30))
                    .build());
            
            // Thêm một thông báo cũ đã đọc
            notificationRepository.save(Notification.builder()
                    .recipient(student)
                    .syllabus(publicSyllabus)
                    .type(Notification.NotificationType.PDF_UPLOADED)
                    .title("Tài liệu tham khảo mới")
                    .message("File PDF tham khảo cho môn " + publicSyllabus.getCourse().getCourseCode() + " đã được tải lên.")
                    .triggeredBy(publicSyllabus.getLecturer().getUsername())
                    .isRead(true) // Đã đọc
                    .readAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build());
        }

        // 3. Tương tác: Thông báo cho Phòng Đào Tạo (Academic Affairs)
        // Giả lập đề cương đã qua ải HOD, giờ đến lượt PĐT duyệt
        User aaUser = userRepository.findByRole_RoleName("ACADEMIC_AFFAIRS").stream().findFirst().orElse(null);
        if (aaUser != null && !pendingSyllabi.isEmpty()) {
            Syllabus s = pendingSyllabi.get(0);
            notificationRepository.save(Notification.builder()
                    .recipient(aaUser)
                    .syllabus(s)
                    .type(Notification.NotificationType.SYLLABUS_APPROVED_BY_HOD)
                    .title("Đề cương chờ phê duyệt")
                    .message("Đề cương " + s.getCourse().getCourseName() + " đã được Trưởng bộ môn thông qua và đang chờ Phòng Đào Tạo duyệt.")
                    .triggeredBy(head.getUsername())
                    .isRead(false)
                    .createdAt(LocalDateTime.now().minusMinutes(15))
                    .build());
        }
    }

    private void initAiTasks() {
        List<Syllabus> allSyllabi = syllabusRepository.findAll();
        Random rand = new Random();

        for (Syllabus s : allSyllabi) {
            // Randomly assign AI tasks
            if (rand.nextBoolean()) {
                aiTaskRepository.save(AITask.builder()
                        .syllabus(s)
                        .taskType(AITask.TaskType.GENERATE_CLO)
                        .status(AITask.TaskStatus.SUCCESS)
                        .resultSummary("AI created 5 CLOs successfully.")
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build());
            }
            
            if (s.getCurrentStatus() == Syllabus.SyllabusStatus.DRAFT && rand.nextBoolean()) {
                aiTaskRepository.save(AITask.builder()
                        .syllabus(s)
                        .taskType(AITask.TaskType.GRAMMAR_CHECK)
                        .status(AITask.TaskStatus.SUCCESS)
                        .resultSummary("Found 3 typo errors in Description.")
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build());
            }

            if (s.getCurrentStatus() == Syllabus.SyllabusStatus.PENDING_REVIEW) {
                aiTaskRepository.save(AITask.builder()
                        .syllabus(s)
                        .taskType(AITask.TaskType.SUGGEST_ASSESSMENT)
                        .status(AITask.TaskStatus.SUCCESS)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }
        log.info("   + Created AI Tasks for random syllabi");
    }

    private void initCourseSubscriptions() {
        log.info("   + Seeding Course Subscriptions...");
        
        // Tìm đối tượng sinh viên (lưu ý dùng đúng username 'student' đã tạo ở initUsers)
        User student = userRepository.findByUsername("student").orElse(null);
        
        // Danh sách các mã môn học muốn sinh viên này đăng ký theo dõi
        String[] courseCodes = {"PRF192", "DBI202", "WED201c"};

        if (student != null) {
            for (String code : courseCodes) {
                courseRepository.findByCourseCode(code).ifPresent(course -> {
                    // Kiểm tra xem sinh viên đã đăng ký môn này chưa để tránh trùng lặp
                    boolean exists = courseSubscriptionRepository
                            .findByUser_UserIdAndCourse_CourseId(student.getUserId(), course.getCourseId())
                            .isPresent();

                    if (!exists) {
                        CourseSubscription sub = CourseSubscription.builder()
                                .user(student)
                                .course(course)
                                .subscribedAt(LocalDateTime.now().minusDays(2))
                                .build();
                        courseSubscriptionRepository.save(sub);
                        log.info("     - Student '{}' subscribed to course: {}", student.getUsername(), code);
                    }
                });
            }
        } else {
            log.warn("   [!] Could not find user 'student' to create subscriptions.");
        }
    }

    private void initReviewComments() {
        // if (reviewCommentRepository.count() > 0) {
        //     return;
        // }

        // 1. Lấy dữ liệu mẫu Syllabus và User để gắn comment
        Syllabus syllabus = syllabusRepository.findAll().stream().findFirst().orElse(null);
        User reviewer = userRepository.findByUsername("head_it").orElse(
            userRepository.findAll().stream().findFirst().orElse(null)
        );

        if (syllabus == null || reviewer == null) {
            System.out.println("Skipping ReviewComment seeding: No Syllabus or User found.");
            return;
        }

        List<ReviewComment> comments = new ArrayList<>();

        // Comment 1: Nhận xét chung về đề cương (General) - Trạng thái đã giải quyết
        comments.add(ReviewComment.builder()
                .content("Cấu trúc đề cương nhìn chung đã ổn. Tuy nhiên cần xem lại phần phân bổ thời gian thực hành cho cân đối hơn.")
                .status(CommentStatus.RESOLVED)
                .contextType(CommentContextType.SYLLABUS_GENERAL)
                .contextId(null) // Không gắn vào phần tử con cụ thể
                .syllabus(syllabus)
                .user(reviewer)
                .resolvedBy(reviewer) // head_it đã resolve
                .resolvedAt(LocalDateTime.now().minusDays(2))
                .resolutionNote("Giảng viên đã điều chỉnh lại thời lượng: tăng thực hành từ 30% lên 45% tổng thời gian. Đã phê duyệt.")
                .build());

        // Comment 2: Nhận xét về Chuẩn đầu ra môn học (CLO) - Trạng thái đang chờ xử lý
        comments.add(ReviewComment.builder()
                .content("CLO 3 đang viết ở mức độ 'Hiểu' (Understand), nhưng bài tập lớn lại yêu cầu mức độ 'Vận dụng' (Apply). Đề nghị nâng mức độ của CLO này lên.")
                .status(CommentStatus.OPEN)
                .contextType(CommentContextType.CLO)
                .contextId(null) // Giả lập ID của CLO đang được nhận xét
                .syllabus(syllabus)
                .user(reviewer)
                .build());

        // Comment 3: Nhận xét về Kế hoạch giảng dạy (Session Plan) - Đang chờ
        comments.add(ReviewComment.builder()
                .content("Nội dung tuần 5 quá nặng lý thuyết. Nên bổ sung thêm hoạt động thảo luận nhóm (Group discussion) để sinh viên đỡ nhàm chán.")
                .status(CommentStatus.OPEN)
                .contextType(CommentContextType.SESSION_PLAN)
                .contextId(null) // Giả lập ID của buổi học
                .syllabus(syllabus)
                .user(reviewer)
                .build());

        // Comment 4: Nhận xét về Tài liệu tham khảo (Material) - Đã xong
        comments.add(ReviewComment.builder()
                .content("Link tài liệu tham khảo số [2] bị lỗi 404, giảng viên vui lòng cập nhật link mới hoặc thay thế bằng sách giáo trình 2024.")
                .status(CommentStatus.RESOLVED)
                .contextType(CommentContextType.MATERIAL)
                .contextId(null)
                .syllabus(syllabus)
                .user(reviewer)
                .resolvedBy(reviewer) // head_it đã resolve
                .resolvedAt(LocalDateTime.now().minusDays(1))
                .resolutionNote("Giảng viên đã thay link mới: 'Introduction to Java Programming, 12th Edition (2024)' - link Google Drive đã được kiểm tra. OK.")
                .build());

        // Comment 5: Nhận xét về Đánh giá (Assessment) - Đang chờ
        comments.add(ReviewComment.builder()
                .content("Tỉ lệ điểm quá trình 50% là hơi cao so với quy định mới của khoa. Đề nghị điều chỉnh về 40% - 60%.")
                .status(CommentStatus.OPEN)
                .contextType(CommentContextType.ASSESSMENT)
                .contextId(null)
                .syllabus(syllabus)
                .user(reviewer)
                .build());

        // Comment 6: Một comment tích cực (General)
        comments.add(ReviewComment.builder()
                .content("Phần Đạo đức nghề nghiệp (Professional Ethics) được lồng ghép rất hay. Duyệt phần này.")
                .status(CommentStatus.RESOLVED)
                .contextType(CommentContextType.SYLLABUS_GENERAL)
                .contextId(null)
                .syllabus(syllabus)
                .user(reviewer)
                .resolvedBy(reviewer) // head_it đã resolve
                .resolvedAt(LocalDateTime.now().minusDays(3))
                .resolutionNote("Không cần chỉnh sửa. Approved.")
                .build());

        reviewCommentRepository.saveAll(comments);
        System.out.println("Seeded " + comments.size() + " review comments.");
    }

    // ==========================================
    // PHẦN BỔ SUNG: REPORT, WORKFLOW & AUDIT
    // ==========================================

    private void initReports() {
        if (reportRepository.count() > 0) return;

        User student = userRepository.findByUsername("student").orElse(null);
        if (student == null) return;

        List<Report> reports = new ArrayList<>();

        // Report 1: Lỗi kỹ thuật (Chưa xử lý)
        reports.add(Report.builder()
                .title("Lỗi không tải được file PDF đề cương môn Java")
                .description("Em vào mục môn học JPD113 nhưng bấm nút tải về thì báo lỗi 404. Nhờ admin kiểm tra giúp.")
                .status(Report.ReportStatus.PENDING)
                .reporter(student)
                .build());

        // Report 2: Góp ý (Đã xem)
        reports.add(Report.builder()
                .title("Góp ý về giao diện Dark Mode")
                .description("Giao diện tối đôi khi bị lỗi màu chữ ở phần Comment, chữ đen trên nền xám khó đọc.")
                .status(Report.ReportStatus.REVIEWED)
                .reporter(student)
                .build());

        // Report 3: Đã giải quyết
        reports.add(Report.builder()
                .title("Không đăng nhập được bằng email sinh viên")
                .description("Em đã đổi mật khẩu nhưng không login được.")
                .status(Report.ReportStatus.RESOLVED)
                .reporter(student)
                .createdAt(LocalDateTime.now().minusDays(10)) // Report cũ
                .build());

        reportRepository.saveAll(reports);
        log.info("   + Created {} Reports.", reports.size());
    }

    private void initWorkflowSteps() {
        if (workflowStepRepository.count() > 0) return;

        // Tạo các bước quy trình chuẩn
        workflowStepRepository.save(WorkflowStep.builder().stepName("DRAFT").stepOrder(1).build());
        workflowStepRepository.save(WorkflowStep.builder().stepName("REVIEW_REQUESTED").stepOrder(2).build());
        workflowStepRepository.save(WorkflowStep.builder().stepName("HEAD_APPROVED").stepOrder(3).build());
        workflowStepRepository.save(WorkflowStep.builder().stepName("PUBLISHED").stepOrder(4).build());
        
        log.info("   + Created Workflow Steps.");
    }

    private void initWorkflowHistory() {
        // Lấy Syllabus đã Published để giả lập lịch sử đầy đủ
        Syllabus syllabus = syllabusRepository.findAll().stream()
                .filter(s -> s.getCurrentStatus() == Syllabus.SyllabusStatus.PUBLISHED)
                .findFirst().orElse(null);

        if (syllabus == null) return;

        User lecturer = syllabus.getLecturer();
        User head = syllabus.getCourse().getDepartment().getHeadOfDepartment();
        User admin = userRepository.findByUsername("admin").orElse(null);
        
        // Lấy các Step
        WorkflowStep stepDraft = workflowStepRepository.findAll().stream().filter(s -> s.getStepOrder() == 1).findFirst().orElse(null);
        WorkflowStep stepReview = workflowStepRepository.findAll().stream().filter(s -> s.getStepOrder() == 2).findFirst().orElse(null);
        WorkflowStep stepApprove = workflowStepRepository.findAll().stream().filter(s -> s.getStepOrder() == 3).findFirst().orElse(null);
        WorkflowStep stepPublish = workflowStepRepository.findAll().stream().filter(s -> s.getStepOrder() == 4).findFirst().orElse(null);

        List<SyllabusWorkflowHistory> histories = new ArrayList<>();

        // 1. Giảng viên tạo và Submit
        histories.add(SyllabusWorkflowHistory.builder()
                .syllabus(syllabus)
                .workflowStep(stepDraft)
                .actionBy(lecturer)
                .action(SyllabusWorkflowHistory.WorkflowAction.SUBMIT)
                .comment("Đã hoàn thành soạn thảo, xin trưởng bộ môn duyệt.")
                .actionTime(LocalDateTime.now().minusDays(5))
                .build());

        // 2. Trưởng bộ môn Duyệt
        if (head != null) {
            histories.add(SyllabusWorkflowHistory.builder()
                    .syllabus(syllabus)
                    .workflowStep(stepReview)
                    .actionBy(head)
                    .action(SyllabusWorkflowHistory.WorkflowAction.APPROVE)
                    .comment("Nội dung tốt, đồng ý thông qua.")
                    .actionTime(LocalDateTime.now().minusDays(3))
                    .build());
        }

        // 3. Admin Public
        if (admin != null) {
            histories.add(SyllabusWorkflowHistory.builder()
                    .syllabus(syllabus)
                    .workflowStep(stepPublish)
                    .actionBy(admin)
                    .action(SyllabusWorkflowHistory.WorkflowAction.PUBLISH)
                    .comment("Đã xuất bản lên hệ thống cho sinh viên đăng ký.")
                    .actionTime(LocalDateTime.now().minusDays(1))
                    .build());
        }

        workflowHistoryRepository.saveAll(histories);
        log.info("   + Created Workflow History for Syllabus: {}", syllabus.getCourse().getCourseCode());
    }

    private void initAuditLogs() {
        // Lấy ngẫu nhiên một syllabus
        Syllabus syllabus = syllabusRepository.findAll().stream().findFirst().orElse(null);
        if (syllabus == null) return;

        List<SyllabusAuditLog> logs = new ArrayList<>();

        // Log 1: Tạo mới
        logs.add(SyllabusAuditLog.builder()
                .syllabus(syllabus)
                .actionType(SyllabusAuditLog.AuditAction.CREATE_SYLLABUS.name())
                .performedBy(syllabus.getLecturer().getUsername())
                .performedByRole("LECTURER")
                .oldStatus("NULL")
                .newStatus("DRAFT")
                .comments("Initial creation")
                .ipAddress("192.168.1.10")
                .userAgent("Mozilla/5.0 (Windows NT 10.0)")
                .build());

        // Log 2: Cập nhật nội dung
        logs.add(SyllabusAuditLog.builder()
                .syllabus(syllabus)
                .actionType(SyllabusAuditLog.AuditAction.UPDATE_SYLLABUS.name())
                .performedBy(syllabus.getLecturer().getUsername())
                .performedByRole("LECTURER")
                .oldStatus("DRAFT")
                .newStatus("DRAFT")
                .changedFields("{\"field\": \"assessment\", \"old\": \"20%\", \"new\": \"30%\"}") // Giả lập JSON
                .ipAddress("192.168.1.10")
                .build());

        // Log 3: Upload PDF
        logs.add(SyllabusAuditLog.builder()
                .syllabus(syllabus)
                .actionType(SyllabusAuditLog.AuditAction.UPLOAD_PDF.name())
                .performedBy(syllabus.getLecturer().getUsername())
                .performedByRole("LECTURER")
                .additionalData("file_name: syllabus_v1.pdf, size: 2MB")
                .build());

        auditLogRepository.saveAll(logs);
        log.info("   + Created Audit Logs.");
    }
}