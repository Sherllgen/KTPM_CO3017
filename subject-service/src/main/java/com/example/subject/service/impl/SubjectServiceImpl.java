package com.example.subject.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.subject.client.UserServiceClient;
import com.example.subject.config.ApiResponse;
import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.InstructorAssignmentDto;
import com.example.subject.dto.response.PageResponse;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.dto.response.UserValidationDto;
import com.example.subject.exception.AppException;
import com.example.subject.exception.ErrorCode;
import com.example.subject.mapper.SubjectMapper;
import com.example.subject.model.Subject;
import com.example.subject.model.SubjectInstructorAssignment;
import com.example.subject.model.enums.SubjectStatus;
import com.example.subject.repository.SubjectInstructorAssignmentRepository;
import com.example.subject.repository.SubjectRepository;
import com.example.subject.service.SubjectService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final UserServiceClient userServiceClient;
    private final SubjectInstructorAssignmentRepository assignmentRepository;

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
                dtoPage.isLast());
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

    @Override
    @Transactional
    public InstructorAssignmentDto assignInstructor(Long subjectId, Long instructorId) {
        log.info("Assigning instructor {} to subject {}", instructorId, subjectId);

        // 1. Validate subject exists
        Subject subject = subjectRepository.findBySubjectId(subjectId);
        if (subject == null) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        // 2. Check if instructor is already assigned
        if (assignmentRepository.existsBySubjectSubjectIdAndInstructorId(subjectId, instructorId)) {
            throw new AppException(ErrorCode.INSTRUCTOR_ALREADY_ASSIGNED);
        }

        // 3. Validate instructor via Feign Client
        UserValidationDto userValidation;
        try {
            ResponseEntity<ApiResponse<UserValidationDto>> response = userServiceClient
                    .validateInstructor(instructorId);

            if (response.getBody() == null || response.getBody().getData() == null) {
                throw new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND);
            }

            userValidation = response.getBody().getData();

        } catch (FeignException.NotFound e) {
            log.error("Instructor not found: {}", instructorId);
            throw new AppException(ErrorCode.INSTRUCTOR_NOT_FOUND);
        } catch (FeignException e) {
            log.error("Error calling user service: {}", e.getMessage());
            throw new AppException(ErrorCode.USER_SERVICE_UNAVAILABLE);
        }

        // 4. Validate user is an instructor and active
        if (!userValidation.getIsInstructor()) {
            throw new AppException(ErrorCode.INSTRUCTOR_NOT_VALID);
        }

        // 5. Create assignment
        SubjectInstructorAssignment assignment = SubjectInstructorAssignment.builder()
                .subject(subject)
                .instructorId(instructorId)
                .instructorName(userValidation.getFullName())
                .build();

        SubjectInstructorAssignment savedAssignment = assignmentRepository.save(assignment);

        log.info("Successfully assigned instructor {} to subject {}", instructorId, subjectId);

        return InstructorAssignmentDto.builder()
                .subjectInstructorAssignmentId(savedAssignment.getSubjectInstructorAssignmentId())
                .subjectId(subjectId)
                .instructorId(instructorId)
                .instructorName(userValidation.getFullName())
                .build();
    }

    @Override
    @Transactional
    public void removeInstructor(Long subjectId, Long instructorId) {
        log.info("Removing instructor {} from subject {}", instructorId, subjectId);

        // 1. Validate subject exists
        Subject subject = subjectRepository.findBySubjectId(subjectId);
        if (subject == null) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        // 2. Find assignment
        SubjectInstructorAssignment assignment = assignmentRepository
                .findBySubjectSubjectIdAndInstructorId(subjectId, instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.INSTRUCTOR_NOT_ASSIGNED));

        // 3. Delete assignment
        assignmentRepository.delete(assignment);

        log.info("Successfully removed instructor {} from subject {}", instructorId, subjectId);
    }

    @Override
    @Transactional
    public void removeInstructorFromAllSubjects(Long instructorId) {
        log.info("Removing instructor {} from all subjects", instructorId);
        assignmentRepository.deleteByInstructorId(instructorId);
    }
}
