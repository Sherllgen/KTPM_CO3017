package com.example.subject.service;

import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.InstructorAssignmentDto;
import com.example.subject.dto.response.PageResponse;
import com.example.subject.dto.response.SubjectDto;

public interface SubjectService {
    SubjectDto createSubject(SubjectCreateRequest request);

    SubjectDto updateSubject(SubjectUpdateRequest request, Long subjectId);

    PageResponse<SubjectDto> getAllSubjects(int page, int size);

    SubjectDto getSubjectById(Long subjectId);

    void deleteSubjectById(Long subjectId);

    // Instructor assignment methods
    InstructorAssignmentDto assignInstructor(Long subjectId, Long instructorId);

    void removeInstructor(Long subjectId, Long instructorId);
}
