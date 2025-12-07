package com.example.subject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.subject.config.ApiResponse;
import com.example.subject.service.SubjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/internal/subjects")
@RequiredArgsConstructor
@Tag(name = "Subject Internal Controller", description = "Internal APIs for inter-service communication")
public class SubjectInternalController {

    private final SubjectService subjectService;

    @DeleteMapping("/instructors/{instructorId}")
    @Operation(summary = "Remove instructor from all subjects", description = "Internal endpoint to remove an instructor from all subjects upon user deletion.")
    public ResponseEntity<ApiResponse<Void>> removeInstructorFromAllSubjects(@PathVariable Long instructorId) {
        subjectService.removeInstructorFromAllSubjects(instructorId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Successfully removed instructor from all subjects")
                .build());
    }
}
