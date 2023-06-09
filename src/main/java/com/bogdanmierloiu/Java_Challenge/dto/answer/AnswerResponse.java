package com.bogdanmierloiu.Java_Challenge.dto.answer;

import com.bogdanmierloiu.Java_Challenge.dto.player.PlayerResponse;
import com.bogdanmierloiu.Java_Challenge.dto.question.QuestionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerResponse {

    private Long id;

    private String text;

    private Boolean isValid;

    private QuestionResponse question;

    private PlayerResponse player;


}
