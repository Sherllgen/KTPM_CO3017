package com.example.subject.dto.request;

import com.example.subject.model.enums.SubjectStatus;

public record SubjectUpdateRequest(
    String name,
    String description,
    String code,
    SubjectStatus status
) {

}