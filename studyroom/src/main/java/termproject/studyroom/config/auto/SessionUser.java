package termproject.studyroom.config.auto;

import lombok.Setter;
import termproject.studyroom.model.Grade;
import lombok.Getter;
import termproject.studyroom.domain.User;

import java.io.Serializable;

@Setter
@Getter
public class SessionUser implements Serializable { // 직렬화 기능을 가진 세션 DTO

    // 인증된 사용자 정보만 필요 => name, email, seqId 필드만 선언
    private String name;
    private Integer stdId;
    private String email;
    private Grade grade;

    public SessionUser(User user) {
        this.name = user.getName();
        this.grade = user.getGrade();
        this.email = user.getEmail();
        this.stdId = user.getStdId();

    }
}