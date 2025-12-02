package com.example.subject.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.PageResponse;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.exception.AppException;
import com.example.subject.exception.ErrorCode;
import com.example.subject.mapper.SubjectMapper;
import com.example.subject.model.Subject;
import com.example.subject.model.enums.SubjectStatus;
import com.example.subject.repository.SubjectRepository;
import com.example.subject.service.SubjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional
    public SubjectDto createSubject(SubjectCreateRequest request) {
        if (subjectRepository.existsByName(request.name())) {
            throw new AppException(ErrorCode.SUBJECT_NAME_EXISTED);
        }
        Subject subject = Subject.builder()
                .name(request.name())
                .code(request.code())
                .level(request.level())
                .description(request.description())
                .status(SubjectStatus.DRAFT)
                .build();
        Subject savedSubject = subjectRepository.save(subject);

        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public SubjectDto updateSubject(SubjectUpdateRequest request, Long subjectId) {
        Subject subject = subjectRepository.findBySubjectId(subjectId);

        if (subject == null) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        if (request.code() != null && !request.code().equals(subject.getCode())) {
            if (subjectRepository.existsByCode(request.code())) {
                throw new AppException(ErrorCode.SUBJECT_CODE_EXISTED);
            }
            subject.setCode(request.code());
        }

        if (request.name() != null && !request.name().equals(subject.getName())) {
            if (subjectRepository.existsByName(request.name())) {
                throw new AppException(ErrorCode.SUBJECT_NAME_EXISTED);
            }
            subject.setName(request.name());
        }

        subjectMapper.updateSubjectFromRequest(request, subject);

        Subject updatedSubject = subjectRepository.save(subject);

        return subjectMapper.toDto(updatedSubject);
    }

    @Override
    public PageResponse<SubjectDto> getAllSubjects(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Subject> subjectPage = subjectRepository.findAll(pageable);
        Page<SubjectDto> dtoPage = subjectPage.map(subjectMapper::toDto);

        return new PageResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),         
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.isLast()
        );
    }

    @Override
    public SubjectDto getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findBySubjectId(subjectId);
        if (subject == null) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);   
        }
        return subjectMapper.toDto(subject);
    }

    @Override
    @Transactional
    public void deleteSubjectById(Long subjectId) {
        if (subjectRepository.findBySubjectId(subjectId) == null) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        subjectRepository.deleteById(subjectId);
    }
}
