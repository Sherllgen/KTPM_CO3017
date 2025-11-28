package com.example.subject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.subject.model.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findByTopicId(Long topicId);
    void deleteByTopicId(Long topicId);
}
