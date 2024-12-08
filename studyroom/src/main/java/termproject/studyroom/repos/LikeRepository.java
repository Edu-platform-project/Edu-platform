package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.Like;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.BoardType;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findFirstByUser(User user);
    Integer countByPostIdAndBoardType(Integer postId, BoardType boardType);

    Like findByUserAndPostIdAndBoardType(User user, Integer postId, BoardType boardType);

    boolean existsByUserAndPostId(User user, Integer postId);

    int countByPostId(Integer postId);
}
