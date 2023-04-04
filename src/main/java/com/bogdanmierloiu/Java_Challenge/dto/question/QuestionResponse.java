package com.bogdanmierloiu.Java_Challenge.dto.question;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;

    private Boolean isResolved;

    private Long rewardTokens;

    private String text;

    public QuestionResponse(Long id, String text) {
        this.id = id;
        this.text = getText();
    }
}
