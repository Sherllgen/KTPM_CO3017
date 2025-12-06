package com.example.subject.controller;

import com.example.subject.dto.response.TopicDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.subject.config.ApiResponse;
import com.example.subject.dto.response.PageResponse;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.service.SubjectService;
import com.example.subject.service.TopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class SubjectController {
    private final SubjectService subjectService;
    private final TopicService topicService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INSTRUCTOR', 'SYSTEM')")
    @Operation(summary = "Get all subjects", description = "Retrieves a list of all subjects.")
    public ResponseEntity<ApiResponse<PageResponse<SubjectDto>>> getSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<SubjectDto> subjects = subjectService.getAllSubjects(page, size);

        return ResponseEntity.ok(ApiResponse.<PageResponse<SubjectDto>>builder()
                .status(200)
                .data(subjects)
                .message("Subjects retrieved successfully")
                .build());
    }

    @GetMapping("/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INSTRUCTOR', 'SYSTEM')")
    @Operation(summary = "Get a subject by ID", description = "Retrieves a subject by its ID.")
    public ResponseEntity<ApiResponse<SubjectDto>> getSubjectById(@PathVariable Long subjectId) {

        SubjectDto subject = subjectService.getSubjectById(subjectId);

        return ResponseEntity.ok(ApiResponse.<SubjectDto>builder()
                .status(200)
                .data(subject)
                .message("Subject retrieved successfully")
                .build());

    }

    @GetMapping("/{subjectId}/topics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INSTRUCTOR', 'SYSTEM')")
    @Operation(summary = "Get topics for a subject", description = "Retrieves topics associated with a specific subject.")
    public ResponseEntity<ApiResponse<java.util.List<TopicDto>>> getTopicsForSubject(
            @PathVariable Long subjectId) {
        List<TopicDto> result = topicService.getTopicsBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.<List<TopicDto>>builder()
                .status(200)
                .data(result)
                .message("Topics retrieved for subject successfully")
                .build());
    }

}
