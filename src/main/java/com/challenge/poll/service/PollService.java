package com.challenge.poll.service;

import com.challenge.poll.model.ChoiceVoteCount;
import com.challenge.poll.model.jpa.Choice;
import com.challenge.poll.model.jpa.Poll;
import com.challenge.poll.model.jpa.User;
import com.challenge.poll.model.jpa.Vote;
import com.challenge.poll.payload.request.PollRequest;
import com.challenge.poll.payload.request.VoteRequest;
import com.challenge.poll.payload.response.PollResponse;
import com.challenge.poll.repository.PollRepository;
import com.challenge.poll.repository.UserRepository;
import com.challenge.poll.repository.VoteRepository;
import com.challenge.poll.security.CustomUserDetails;
import com.challenge.poll.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(PollService.class);

    public List<PollResponse> getAllPolls() {

        List<Poll> polls = pollRepository.findAll();
        List<Long> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());

        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls);

        List<PollResponse> pollResponses = new ArrayList<PollResponse>();

        polls.forEach(poll -> {
            PollResponse pollResponse = ModelMapper.mapPollToPollResponse( poll, choiceVoteCountMap, creatorMap.get( poll.getCreatedBy()));
            pollResponses.add(pollResponse);
        });

        return pollResponses;
    }

    public Poll createPoll(PollRequest pollRequest) {
        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoices().forEach(choiceRequest -> {
            poll.addChoice(new Choice(choiceRequest.getText()));
        });

        return pollRepository.save(poll);
    }

    public PollResponse getPollById(Long pollId) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(
                () -> new RuntimeException(String.format("Poll not found with id : '%s'", pollId)));

        // Retrieve Vote Counts of every choice belonging to the current poll
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        // Retrieve poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new RuntimeException(String.format("User not found with id : '%s'", poll.getCreatedBy())));

        // Retrieve vote done by logged in user
        PollResponse pollResponse = new PollResponse();


        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap,  creator);
    }

    public PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, CustomUserDetails currentUser) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException(String.format("Poll not found with id : '%s'", pollId)));

        User user = userRepository.getOne(currentUser.getId());

        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Choice not found with id : '%s'", voteRequest.getChoiceId())));

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(user);
        vote.setChoice(selectedChoice);

        try {
            vote = voteRepository.save(vote);
        } catch (DataIntegrityViolationException ex) {
            logger.info("User {} has already voted in Poll {}", currentUser.getId(), pollId);
            throw new RuntimeException("Sorry! You have already cast your vote in this poll");
        }

        //-- Vote Saved, Return the updated Poll Response now --

        // Retrieve Vote Counts of every choice belonging to the current poll
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        // Retrieve poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new RuntimeException(String.format("User not found with id : '%s'", poll.getCreatedBy())));

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator);
    }

    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds) {
        // Retrieve Vote Counts of every Choice belonging to the given pollIds
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        return choiceVotesMap;
    }

    private Map<Long, Long> getPollUserVoteMap(CustomUserDetails currentUser, List<Long> pollIds) {
        // Retrieve Votes done by the logged in user to the given pollIds
        Map<Long, Long> pollUserVoteMap = null;
        if(currentUser != null) {
            List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);

            pollUserVoteMap = userVotes.stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
        }
        return pollUserVoteMap;
    }

    Map<Long, User> getPollCreatorMap(List<Poll> polls) {
        // Get Poll Creator details of the given list of polls
        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }
}