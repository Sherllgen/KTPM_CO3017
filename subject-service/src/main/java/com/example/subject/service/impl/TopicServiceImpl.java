package com.example.subject.service.impl;

import com.example.subject.dto.request.TopicCreateRequest;
import com.example.subject.dto.request.TopicUpdateRequest;
import com.example.subject.dto.response.TopicDto;
import com.example.subject.mapper.TopicMapper;
import com.example.subject.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import com.example.subject.exception.AppException;
import com.example.subject.exception.ErrorCode;
import com.example.subject.repository.TopicRepository;
import com.example.subject.service.TopicService;
import com.example.subject.model.Topic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final TopicMapper topicMapper;

    @Override
    @Transactional
    public void deleteTopic(Long topicId) {

        if (topicRepository.findByTopicId(topicId) == null) {
            throw new AppException(ErrorCode.TOPIC_NOT_FOUND);
        }

        topicRepository.deleteByTopicId(topicId);
    }

    @Override
    @Transactional
    public TopicDto createTopic(Long subjectId, TopicCreateRequest topicCreateRequest) {
        Topic topic = Topic.builder()
                .subject(subjectRepository.findBySubjectId(subjectId))
                .title(topicCreateRequest.title())
                .description(topicCreateRequest.description())
                .orderIndex(topicCreateRequest.orderIndex())
                .build();

        Topic savedTopic = topicRepository.save(topic);

        return topicMapper.toDto(savedTopic);
    }

    @Override
    @Transactional
    public TopicDto updateTopic(Long topicId, TopicUpdateRequest topicUpdateRequest) {
        Topic topic = topicRepository.findByTopicId(topicId);

        if (topic == null) {
            throw new AppException(ErrorCode.TOPIC_NOT_FOUND);
        }

        if (topicUpdateRequest.title() != null) {
            topic.setTitle(topicUpdateRequest.title());
        }
        if (topicUpdateRequest.description() != null) {
            topic.setDescription(topicUpdateRequest.description());
        }
        if (topicUpdateRequest.orderIndex() != null) {
            topic.setOrderIndex(topicUpdateRequest.orderIndex());
        }

        Topic updatedTopic = topicRepository.save(topic);

        return topicMapper.toDto(updatedTopic);
    }

    @Override
    public List<TopicDto> getTopicsBySubjectId(Long subjectId) {
        List<Topic> topics = topicRepository.findBySubject_SubjectId(subjectId);
        return topics.stream()
                .sorted(Comparator.comparing(Topic::getOrderIndex))
                .map(topicMapper::toDto)
                .collect(Collectors.toList());
    }

}
