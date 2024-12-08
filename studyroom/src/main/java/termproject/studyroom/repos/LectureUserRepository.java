package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureUser;
import termproject.studyroom.domain.LectureUserId;
import termproject.studyroom.domain.User;

import java.util.List;

@Repository
public interface LectureUserRepository extends JpaRepository<LectureUser, LectureUserId> {

    @Query("SELECT lu.lecture FROM LectureUser lu WHERE lu.user.id = :userId")
    List<LectureList> findLectureListsByUserId(@Param("userId") Integer userId);

    @Query("SELECT l FROM LectureUser l WHERE l.lecture.lectureId = :lectureId")
    List<LectureUser> findByLectureId(@Param("lectureId") Integer lectureId);

}
