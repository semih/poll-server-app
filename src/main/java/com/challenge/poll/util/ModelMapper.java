package com.challenge.poll.util;

import com.challenge.poll.model.jpa.Poll;
import com.challenge.poll.model.jpa.User;
import com.challenge.poll.payload.response.ChoiceResponse;
import com.challenge.poll.payload.response.PollResponse;
import com.challenge.poll.payload.response.UserSummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {
    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());

        List<ChoiceResponse> choiceResponses = poll.getChoices().stream().map(choice -> {
            ChoiceResponse choiceResponse = new ChoiceResponse();
            choiceResponse.setId(choice.getId());
            choiceResponse.setText(choice.getText());

            if(choiceVotesMap.containsKey(choice.getId())) {
                choiceResponse.setVoteCount(choiceVotesMap.get(choice.getId()));
            } else {
                choiceResponse.setVoteCount(0);
            }
            return choiceResponse;
        }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);
        /*
        UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(creatorSummary);
        */
        return pollResponse;
    }
}
