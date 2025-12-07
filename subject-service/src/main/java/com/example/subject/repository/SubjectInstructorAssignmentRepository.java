package com.example.subject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.subject.model.SubjectInstructorAssignment;

@Repository
public interface SubjectInstructorAssignmentRepository extends JpaRepository<SubjectInstructorAssignment, Long> {

    boolean existsBySubjectSubjectIdAndInstructorId(Long subjectId, Long instructorId);

    Optional<SubjectInstructorAssignment> findBySubjectSubjectIdAndInstructorId(Long subjectId, Long instructorId);

    void deleteByInstructorId(Long instructorId);
}
