package com.example.subject.model;

import java.util.Set;

import com.example.subject.model.enums.SubjectStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter 
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    private String level;

    @Enumerated(EnumType.STRING)
    private SubjectStatus status;

    @JsonManagedReference
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Topic> topics;

    @JsonManagedReference
    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private Set<SubjectInstructorAssignment> subjectInstructorAssignments;
}
