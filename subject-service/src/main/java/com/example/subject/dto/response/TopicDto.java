package com.example.subject.dto.response;

public record TopicDto(
    Long id,
    Long subjectId,
    String title,
    String description,
    Integer orderIndex
) {
}
