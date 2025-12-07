package com.example.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.user.config.ApiResponse;

@FeignClient(name = "subject-service")
public interface SubjectClient {

    @DeleteMapping("/api/internal/subjects/instructors/{instructorId}")
    ResponseEntity<ApiResponse<Void>> removeInstructorFromAllSubjects(@PathVariable("instructorId") Long instructorId);
}
