package termproject.studyroom.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.*;
import termproject.studyroom.model.BoardType;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.repos.*;
import termproject.studyroom.util.NotFoundException;


@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final CommunicationBoardRepository communicationBoardRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final LectureRequestRepository lectureRequestRepository;

    public LikeService(final LikeRepository likeRepository,
                       final UserRepository userRepository, QuestionBoardRepository questionBoardRepository, CommunicationBoardRepository communicationBoardRepository, SharingBoardRepository sharingBoardRepository, LectureRequestRepository lectureRequestRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
        this.communicationBoardRepository = communicationBoardRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.lectureRequestRepository = lectureRequestRepository;
    }

    public Integer addLike(LikeDTO likeDTO) {
        // User 확인
        User user = userRepository.findById(likeDTO.getAuthor().getStdId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BoardType boardType = likeDTO.getBoardType();

        // 공통 로직: 좋아요 존재 여부 확인
        Like existingLike = likeRepository.findByUserAndPostIdAndBoardType(user, likeDTO.getPostId(), boardType);

        if (existingLike != null) {
            // 좋아요 취소
            likeRepository.delete(existingLike);
            updateLikeCount(likeDTO.getPostId(), boardType, -1);
            return -1; // 좋아요 취소됨
        } else {
            if (boardType == BoardType.COMMUNICATION_BOARD) {
                CommunicationBoard communicationBoard = communicationBoardRepository.findById(likeDTO.getPostId())
                        .orElseThrow(() -> new IllegalArgumentException("CommunicationBoard not found"));

                // 좋아요 수 제한 확인
                if (communicationBoard.getLikeCount() >= communicationBoard.getMaxnum()) {
                    throw new IllegalStateException("Maximum likes reached");
                }
            }

            // 좋아요 추가
            Like like = new Like();
            like.setUser(user);
            like.setPostId(likeDTO.getPostId());
            like.setBoardType(boardType);
            likeRepository.save(like);

            updateLikeCount(likeDTO.getPostId(), boardType, 1);
            return 1; // 좋아요 추가됨
        }
    }

    private void updateLikeCount(Integer postId, BoardType boardType, int increment) {
        switch (boardType) {
            case QUESTION_BOARD -> {
                QuestionBoard questionBoard = questionBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("QuestionBoard not found"));
                questionBoard.setLikeCount(questionBoard.getLikeCount() + increment);
                questionBoardRepository.save(questionBoard);
            }
            case SHARING_BOARD -> {
                SharingBoard sharingBoard = sharingBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("SharingBoard not found"));
                sharingBoard.setLikeCount(sharingBoard.getLikeCount() + increment);
                sharingBoardRepository.save(sharingBoard);
            }
            case COMMUNICATION_BOARD -> {
                CommunicationBoard communicationBoard = communicationBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("CommunicationBoard not found"));
                communicationBoard.setLikeCount(communicationBoard.getLikeCount() + increment);
                communicationBoardRepository.save(communicationBoard);
            }
            case LECTURE_REQUEST -> {
                LectureRequest lectureRequest = lectureRequestRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("LectureRequest not found"));
                lectureRequest.setLikeCount(lectureRequest.getLikeCount() + increment);
                lectureRequestRepository.save(lectureRequest);
            }

            default -> throw new IllegalArgumentException("Unsupported BoardType");
        }
    }

    public int getLikeCount(Integer postId) {
        return likeRepository.countByPostId(postId);
    }

    public List<String> getLikerNames(Integer postId, BoardType boardType) {
        List<Like> likes = likeRepository.findByPostIdAndBoardType(postId, boardType);
        return likes.stream()
                .map(like -> like.getUser().getName()) // 사용자 이름 가져오기
                .toList();
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

