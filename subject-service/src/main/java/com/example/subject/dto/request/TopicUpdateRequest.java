package com.example.subject.dto.request;

public record TopicUpdateRequest(
        String title,
        String description,
        Integer orderIndex) {
}