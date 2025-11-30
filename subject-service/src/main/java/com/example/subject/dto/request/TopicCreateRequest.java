package com.example.subject.dto.request;

public record TopicCreateRequest(
        String title,
        String description,
        Integer orderIndex) {
}
