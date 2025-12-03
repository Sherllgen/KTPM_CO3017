package com.example.subject.mapper;

import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.model.Subject;

public interface SubjectMapper {
    SubjectDto toDto(Subject subject);
    Subject fromCreateRequest(SubjectCreateRequest request);
    void updateSubjectFromRequest(SubjectUpdateRequest request, Subject subject);
}
