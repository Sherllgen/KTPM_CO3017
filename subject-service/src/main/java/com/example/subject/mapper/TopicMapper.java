package com.example.subject.mapper;

import com.example.subject.dto.request.TopicCreateRequest;
import com.example.subject.dto.request.TopicUpdateRequest;
import com.example.subject.dto.response.TopicDto;
import com.example.subject.model.Topic;

public interface TopicMapper {
    TopicDto toDto(Topic topic);
    Topic fromCreateRequest(Long subjectId, TopicCreateRequest request);
    void updateTopicFromRequest(TopicUpdateRequest request, Topic topic);
}
