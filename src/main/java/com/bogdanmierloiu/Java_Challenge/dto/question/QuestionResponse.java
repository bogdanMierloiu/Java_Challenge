package com.bogdanmierloiu.Java_Challenge.dto.question;

import com.bogdanmierloiu.Java_Challenge.dto.player.PlayerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionResponse {
    private Long id;

    private Boolean isResolved;

    private Long rewardTokens;

    private String text;

    private PlayerResponse player;

}
