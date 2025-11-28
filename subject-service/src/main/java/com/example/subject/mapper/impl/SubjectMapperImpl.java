package com.example.subject.mapper.impl;

import org.springframework.stereotype.Component;

import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.mapper.SubjectMapper;
import com.example.subject.model.Subject;
import com.example.subject.model.enums.SubjectStatus;

@Component
public class SubjectMapperImpl implements SubjectMapper {
    
    @Override
    public SubjectDto toDto(Subject subject) {
        if (subject == null) {
            return null;
        }
        return new SubjectDto(
            subject.getSubjectId(),
            subject.getCode(),
            subject.getName(),
            subject.getDescription(),
            subject.getLevel(),
            subject.getStatus() != null ? subject.getStatus().name() : null
        );
    }

    @Override
    public Subject fromCreateRequest(SubjectCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        Subject subject = new Subject();
        subject.setName(request.name());
        subject.setDescription(request.description());
        subject.setCode(request.code());
        subject.setLevel(request.level());
        subject.setStatus(SubjectStatus.DRAFT); // Default status for new subjects
        
        return subject;
    }

    @Override
    public void updateSubjectFromRequest(SubjectUpdateRequest request, Subject subject) {
        if (request == null || subject == null) {
            return;
        }

        if (request.description() != null) {
            subject.setDescription(request.description());
        }
        if (request.status() != null) {
            subject.setStatus(request.status());
        }
    }
    
}
