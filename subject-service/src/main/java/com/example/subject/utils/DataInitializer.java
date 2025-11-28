package com.example.subject.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.subject.model.Subject;
import com.example.subject.model.Topic;
import com.example.subject.model.enums.SubjectStatus;
import com.example.subject.repository.SubjectRepository;
import com.example.subject.repository.TopicRepository;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    public DataInitializer(SubjectRepository subjectRepository, TopicRepository topicRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        long count = subjectRepository.count();
        System.out.println("Subject count in DB = " + count);
        if (count == 0) {
            System.out.println("Initializing sample subjects...");
            initializeSubjects();
        } else {
            System.out.println("Skip init subjects, already have data.");
        }
    }

    private void initializeSubjects() {
        Subject[] subjects = {
                Subject.builder()
                        .code("JAVA101")
                        .name("Java Programming")
                        .description("Learn core Java concepts and OOP")
                        .level("Beginner")
                        .status(SubjectStatus.DRAFT)
                        .topics(new HashSet<>(Arrays.asList(
                                Topic.builder().title("Variables and Data Types")
                                        .description("Understanding primitive and reference types").orderIndex(1)
                                        .build(),
                                Topic.builder().title("Object-Oriented Programming")
                                        .description("Classes, inheritance, polymorphism, encapsulation").orderIndex(2)
                                        .build())))
                        .build(),
                Subject.builder()
                        .code("DB201")
                        .name("Database Design")
                        .description("SQL and relational database fundamentals")
                        .level("Intermediate")
                        .status(SubjectStatus.DRAFT)
                        .topics(new HashSet<>(Arrays.asList(
                                Topic.builder().title("Relational Models")
                                        .description("Understanding tables, keys, and relationships").orderIndex(1)
                                        .build(),
                                Topic.builder().title("SQL Queries")
                                        .description("SELECT, INSERT, UPDATE, DELETE operations").orderIndex(2)
                                        .build())))
                        .build(),
                Subject.builder()
                        .code("WEB301")
                        .name("Web Development")
                        .description("HTML, CSS, and JavaScript for web apps")
                        .level("Intermediate")
                        .status(SubjectStatus.DRAFT)
                        .topics(new HashSet<>(Arrays.asList(
                                Topic.builder().title("HTML Basics").description("Semantic HTML and document structure")
                                        .orderIndex(1).build(),
                                Topic.builder().title("CSS Styling")
                                        .description("Layout, flexbox, and responsive design").orderIndex(2).build())))
                        .build(),
                Subject.builder()
                        .code("SA401")
                        .name("Software Architecture")
                        .description("Design patterns and system design")
                        .level("Advanced")
                        .status(SubjectStatus.DRAFT)
                        .topics(new HashSet<>(Arrays.asList(
                                Topic.builder().title("Design Patterns").description("MVC, Singleton, Factory patterns")
                                        .orderIndex(1).build(),
                                Topic.builder().title("Microservices")
                                        .description("Building scalable distributed systems").orderIndex(2).build())))
                        .build(),
                Subject.builder()
                        .code("DS102")
                        .name("Data Structures")
                        .description("Arrays, linked lists, trees, and graphs")
                        .level("Beginner")
                        .status(SubjectStatus.DRAFT)
                        .topics(new HashSet<>(Arrays.asList(
                                Topic.builder().title("Arrays and Lists").description("Linear data structures")
                                        .orderIndex(1).build(),
                                Topic.builder().title("Trees and Graphs")
                                        .description("Hierarchical and network data structures").orderIndex(2)
                                        .build())))
                        .build()
        };

        for (Subject subject : subjects) {
            subject.getTopics().forEach(topic -> topic.setSubject(subject));
            subjectRepository.save(subject);
        }
    }

}
