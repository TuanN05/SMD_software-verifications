package com.smd.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "clo", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"clo_code", "syllabus_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CLO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clo_id")
    private Long cloId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "syllabus_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Syllabus syllabus;

    @Column(name = "clo_code", nullable = false)
    private String cloCode;

    @Column(name = "clo_description", columnDefinition = "TEXT")
    private String cloDescription;

    // Relationships
    @OneToMany(mappedBy = "clo", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<CLOPLOMapping> ploMappings;
}
