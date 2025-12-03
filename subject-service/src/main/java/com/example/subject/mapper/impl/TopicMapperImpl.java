package com.example.subject.mapper.impl;

import com.example.subject.dto.request.TopicCreateRequest;
import com.example.subject.dto.request.TopicUpdateRequest;
import com.example.subject.dto.response.TopicDto;
import com.example.subject.mapper.TopicMapper;
import com.example.subject.model.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapperImpl implements TopicMapper {
    @Override
    public TopicDto toDto(Topic topic) {
        if (topic == null) {
            return null;
        }
        return new TopicDto(
            topic.getTopicId(),
            topic.getSubject().getSubjectId(),
            topic.getTitle(),
            topic.getDescription(),
            topic.getOrderIndex()
        );
    }

    @Override
    public Topic fromCreateRequest(Long subjectId, TopicCreateRequest request) {
        if (request == null) {
            return null;
        }

        Topic topic = new Topic();
        topic.setTitle(request.title());
        topic.setDescription(request.description());
        topic.setOrderIndex(request.orderIndex());

        return topic;
    }

    @Override
    public void updateTopicFromRequest(TopicUpdateRequest request, Topic topic) {
        if (request == null || topic == null) {
            return;
        }

        if (request.description() != null) {
            topic.setDescription(request.description());
        }
        if (request.title() != null) {
            topic.setTitle(request.title());
        }
        if (request.orderIndex() != null) {
            topic.setOrderIndex(request.orderIndex());
        }
    }
}
