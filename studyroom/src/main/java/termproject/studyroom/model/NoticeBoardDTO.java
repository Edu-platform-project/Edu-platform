package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Getter
@Setter
public class NoticeBoardDTO {

    private Integer noticeId;

    @NotNull
    @Size(max = 30)
    private String title;

    @NotNull
    private String content;

    @NotNull
    private User author;

    @NotNull
    private LectureList lectureId;
//
//    @NotNull
//    private LocalDateTime dateCreated;

}
