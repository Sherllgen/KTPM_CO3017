package com.example.subject.service;

import com.example.subject.dto.request.TopicCreateRequest;
import com.example.subject.dto.request.TopicUpdateRequest;
import com.example.subject.dto.response.TopicDto;

import java.util.List;

public interface TopicService {
    void deleteTopic(Long topicId);
    TopicDto createTopic(Long subjectId, TopicCreateRequest topicCreateRequest);
    TopicDto updateTopic(Long topicId, TopicUpdateRequest topicUpdateRequest);
    List<TopicDto> getTopicsBySubjectId(Long subjectId);
} 
