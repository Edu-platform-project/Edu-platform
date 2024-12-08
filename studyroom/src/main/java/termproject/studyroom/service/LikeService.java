package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.Like;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.BoardType;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.repos.LikeRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;

    public LikeService(final LikeRepository likeRepository,
                       final UserRepository userRepository, QuestionBoardRepository questionBoardRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
    }

    public Integer addLike(LikeDTO likeDTO) {
        // User 확인
        User user = userRepository.findById(likeDTO.getAuthor().getStdId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 게시글 확인 (boardType에 따라 게시판 구분)
        BoardType boardType = likeDTO.getBoardType();
        Like existingLike = likeRepository.findByUserAndPostIdAndBoardType(user, likeDTO.getPostId(), boardType);

        if (existingLike != null) {
            // 좋아요 취소
            likeRepository.delete(existingLike);
            // 좋아요 수 감소
            if (boardType == BoardType.QUESTION_BOARD) {
                QuestionBoard questionBoard = questionBoardRepository.findById(likeDTO.getPostId())
                        .orElseThrow(() -> new IllegalArgumentException("QuestionBoard not found"));
                questionBoard.setLikeCount(questionBoard.getLikeCount() - 1);
                questionBoardRepository.save(questionBoard);
            }
            return -1; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            Like like = new Like();
            like.setUser(user);
            like.setPostId(likeDTO.getPostId());
            like.setBoardType(boardType);
            likeRepository.save(like);

            // 좋아요 수 증가
            if (boardType == BoardType.QUESTION_BOARD) {
                QuestionBoard questionBoard = questionBoardRepository.findById(likeDTO.getPostId())
                        .orElseThrow(() -> new IllegalArgumentException("QuestionBoard not found"));
                questionBoard.setLikeCount(questionBoard.getLikeCount() + 1);
                questionBoardRepository.save(questionBoard);
            }
            return 1; // 좋아요 추가됨
        }
    }

    public int getLikeCount(Integer postId) {
        return likeRepository.countByPostId(postId);
    }

    // 모든 좋아요 가져오기
    public List<LikeDTO> findAll() {
        final List<Like> likes = likeRepository.findAll(Sort.by("id"));
        return likes.stream()
                .map(like -> mapToDTO(like, new LikeDTO()))
                .toList();
    }

    // 특정 좋아요 가져오기
    public LikeDTO get(final Integer id) {
        return likeRepository.findById(id)
                .map(like -> mapToDTO(like, new LikeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // 좋아요 추가
    public Integer create(final LikeDTO likeDTO) {
        final Like like = new Like();
        mapToEntity(likeDTO, like);
        return likeRepository.save(like).getId();
    }

    // 좋아요 수정 (필요 시)
    public void update(final Integer id, final LikeDTO likeDTO) {
        final Like like = likeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(likeDTO, like);
        likeRepository.save(like);
    }

    // 좋아요 삭제
    public void delete(final Integer id) {
        likeRepository.deleteById(id);
    }

    // Like Entity -> LikeDTO 변환
    private LikeDTO mapToDTO(final Like like, final LikeDTO likeDTO) {
        likeDTO.setId(like.getId());
        likeDTO.setAuthor(like.getUser());
        likeDTO.setPostId(like.getPostId());
        likeDTO.setBoardType(like.getBoardType());
//        likeDTO.setDateCreated(like.getDateCreated());
//        likeDTO.setLastUpdated(like.getLastUpdated());
        return likeDTO;
    }


    // LikeDTO -> Like Entity 변환
    private Like mapToEntity(final LikeDTO likeDTO, final Like like) {
        final User user = userRepository.findById(likeDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        like.setUser(user);
        like.setPostId(likeDTO.getPostId());
        like.setBoardType(likeDTO.getBoardType());
        return like;
    }

}
