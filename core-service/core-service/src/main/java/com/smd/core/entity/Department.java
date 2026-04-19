package com.smd.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    // Head of Department reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_of_department_id")
    @ToString.Exclude
    @JsonIgnoreProperties({"department", "userRoles", "syllabuses", "reviewComments", "workflowHistories", "notifications", "passwordHash"})
    private User headOfDepartment;

    // Relationships
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties({"department", "syllabuses", "prerequisiteRelations", "relatedToRelations"})
    private List<Course> courses;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties({"department", "plos"})
    private List<Program> programs;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnoreProperties({"department", "userRoles", "syllabuses", "reviewComments", "workflowHistories", "notifications", "passwordHash"})
    private List<User> users;
}
