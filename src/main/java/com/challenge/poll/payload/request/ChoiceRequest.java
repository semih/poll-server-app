package com.challenge.poll.payload.request;

import com.challenge.poll.model.jpa.Poll;

public class ChoiceRequest {

    private Long id;

    private String text;

    private Poll poll;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}