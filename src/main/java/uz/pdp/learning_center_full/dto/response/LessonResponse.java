package uz.pdp.learning_center_full.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pdp.learning_center_full.entity.enums.LessonStatus;


import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonResponse {
    private UUID groupId;
    private Integer lessonNumber;
    private LessonStatus lessonStatus;
    private UUID id;
    private Integer module;
}
