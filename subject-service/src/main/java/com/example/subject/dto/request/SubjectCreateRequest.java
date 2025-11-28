package com.example.subject.dto.request;

public record SubjectCreateRequest(
    String name,
    String description,
    String code,
    String level
) {}
