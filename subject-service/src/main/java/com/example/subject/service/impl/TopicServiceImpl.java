package com.example.subject.service.impl;

import org.springframework.stereotype.Service;

import com.example.subject.exception.AppException;
import com.example.subject.exception.ErrorCode;
import com.example.subject.repository.TopicRepository;
import com.example.subject.service.TopicService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public void deleteTopic(Long topicId) {

        if (topicRepository.findByTopicId(topicId) == null) {
            throw new AppException(ErrorCode.TOPIC_NOT_FOUND);
        }
        
        topicRepository.deleteByTopicId(topicId);
    }
}
