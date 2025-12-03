package com.example.subject.controller;

import com.example.subject.dto.request.AssignInstructorRequest;
import com.example.subject.dto.request.TopicCreateRequest;
import com.example.subject.dto.request.TopicUpdateRequest;
import com.example.subject.dto.response.InstructorAssignmentDto;
import com.example.subject.dto.response.TopicDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/subjects")
@Slf4j
@Tag(name = "Subject Admin Controller", description = "APIs for managing subjects by admin")
@SecurityRequirement(name = "bearerAuth")
public class SubjectAdminController {
        private final SubjectService subjectService;
        private final TopicService topicService;

        @PreAuthorize("hasRole('ADMIN')")
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

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{subjectId}")
        @Operation(summary = "Update an existing subject", description = "Updates an existing subject with the provided details.")
        public ResponseEntity<ApiResponse<SubjectDto>> updateSubject(@PathVariable Long subjectId,
                        @RequestBody SubjectUpdateRequest request) {

                SubjectDto result = subjectService.updateSubject(request, subjectId);

                return ResponseEntity.ok(ApiResponse.<SubjectDto>builder()
                                .status(200)
                                .data(result)
                                .message("Subject updated successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{subjectId}")
        @Operation(summary = "Delete a subject", description = "Deletes a subject by its id along with all associated topics.")
        public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long subjectId) {

                subjectService.deleteSubjectById(subjectId);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(200)
                                .message("Subject and associated topics deleted successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/{subjectId}/topics")
        @Operation(summary = "Create topics for a subject", description = "Creates topics associated with a specific subject.")
        public ResponseEntity<ApiResponse<TopicDto>> createTopicsForSubject(
                        @PathVariable Long subjectId,
                        @RequestBody TopicCreateRequest request) {
                TopicDto result = topicService.createTopic(subjectId, request);

                return ResponseEntity.ok(ApiResponse.<TopicDto>builder()
                                .status(201)
                                .data(result)
                                .message("Topics created for subject successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/topics/{topicId}")
        @Operation(summary = "Update a topic", description = "Updates a specific topic by its id.")
        public ResponseEntity<ApiResponse<TopicDto>> updateTopic(
                        @PathVariable Long topicId,
                        @RequestBody TopicUpdateRequest request) {
                TopicDto result = topicService.updateTopic(topicId, request);
                return ResponseEntity.ok(ApiResponse.<TopicDto>builder()
                                .status(200)
                                .data(result)
                                .message("Topic updated successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/topics/{topicId}")
        @Operation(summary = "Delete a topic", description = "Deletes a specific topic by its id.")
        public ResponseEntity<ApiResponse<Void>> deleteTopic(
                        @PathVariable Long topicId) {
                topicService.deleteTopic(topicId);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(200)
                                .message("Topic deleted successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping("/{subjectId}/instructors")
        @Operation(summary = "Assign instructor to subject", description = "Assigns an instructor to a specific subject. Validates that the user exists and has INSTRUCTOR role via user-service.")
        public ResponseEntity<ApiResponse<InstructorAssignmentDto>> assignInstructor(
                        @PathVariable Long subjectId,
                        @RequestBody @jakarta.validation.Valid AssignInstructorRequest request) {

                InstructorAssignmentDto result = subjectService.assignInstructor(subjectId, request.getInstructorId());

                return ResponseEntity.ok(ApiResponse.<InstructorAssignmentDto>builder()
                                .status(201)
                                .data(result)
                                .message("Instructor assigned to subject successfully")
                                .build());
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{subjectId}/instructors/{instructorId}")
        @Operation(summary = "Remove instructor from subject", description = "Removes an instructor assignment from a specific subject.")
        public ResponseEntity<ApiResponse<Void>> removeInstructor(
                        @PathVariable Long subjectId,
                        @PathVariable Long instructorId) {

                subjectService.removeInstructor(subjectId, instructorId);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(200)
                                .message("Instructor removed from subject successfully")
                                .build());
        }
}
