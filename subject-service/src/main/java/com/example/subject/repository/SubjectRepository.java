package com.example.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.subject.model.Subject;


@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
    Subject findBySubjectId(Long subjectId);
    List<Subject> findAll();
    void deleteBySubjectId(Long subjectId);
}   
