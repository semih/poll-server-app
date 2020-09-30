package com.challenge.poll.payload.request;

import java.time.Instant;
import java.util.List;

public class PollRequest {

    private String question;

    private List<ChoiceRequest> choices;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChoiceRequest> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceRequest> choices) {
        this.choices = choices;
    }

}