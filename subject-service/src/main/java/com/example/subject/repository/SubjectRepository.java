package com.example.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.subject.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
