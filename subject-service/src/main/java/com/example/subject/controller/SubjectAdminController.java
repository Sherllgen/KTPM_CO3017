package com.example.subject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.example.subject.config.ApiResponse;
import com.example.subject.dto.request.SubjectCreateRequest;
import com.example.subject.dto.request.SubjectUpdateRequest;
import com.example.subject.dto.response.SubjectDto;
import com.example.subject.service.SubjectService;
import com.example.subject.service.TopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/subject")
@Slf4j
@Tag(name = "Subject Admin Controller", description = "APIs for managing subjects by admin")
// @SecurityRequirement(name = "bearerAuth")
public class SubjectAdminController {
    private final SubjectService subjectService;
    private final TopicService topicService;

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new subject", description = "Creates a new subject with the provided details.")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@RequestBody SubjectCreateRequest request) {
        
        SubjectDto result = subjectService.createSubject(request);

        return ResponseEntity.ok(ApiResponse.<SubjectDto>builder()
                .status(201)
                .data(result)
                .message("Subject created successfully")
                .build());
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{subjectId}")
    @Operation(summary = "Update an existing subject", description = "Updates an existing subject with the provided details.")
    public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(@PathVariable Long subjectId, @RequestBody SubjectUpdateRequest request) {

        SubjectDto result = subjectService.updateSubject(request, subjectId);

        return ResponseEntity.ok(ApiResponse.<SubjectDto>builder()
                .status(200)
                .data(result)
                .message("Subject updated successfully")
                .build());
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{subjectId}")
    @Operation(summary = "Delete a subject", description = "Deletes a subject by its id along with all associated topics.")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long subjectId) {

        subjectService.deleteSubjectById(subjectId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Subject and associated topics deleted successfully")
                .build());
    }

}
