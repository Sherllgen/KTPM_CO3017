package com.example.subject.dto.response;

public record SubjectDto(
    Long id,
    String code,
    String name,
    String description,
    String level,
    String status
) {
} 
