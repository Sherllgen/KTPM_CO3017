package com.example.subject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorAssignmentDto {
    private Long subjectInstructorAssignmentId;
    private Long subjectId;
    private Long instructorId;
    private String instructorName;
}
