package termproject.studyroom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.service.LikeService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 좋아요 추가 요청
    @PostMapping("/add")
    public ResponseEntity<Map<String, Integer>> addLike(@RequestBody LikeDTO likeDTO) {
        likeService.addLike(likeDTO);
        int likeCount = likeService.getLikeCount(likeDTO.getPostId());
        Map<String, Integer> response = new HashMap<>();
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }

}
