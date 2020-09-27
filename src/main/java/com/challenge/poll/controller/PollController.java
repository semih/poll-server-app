package com.challenge.poll.controller;

import com.challenge.poll.model.jpa.Poll;
import com.challenge.poll.payload.request.PollRequest;
import com.challenge.poll.payload.request.VoteRequest;
import com.challenge.poll.payload.response.ApiResponse;
import com.challenge.poll.payload.response.PollResponse;
import com.challenge.poll.repository.PollRepository;
import com.challenge.poll.repository.UserRepository;
import com.challenge.poll.repository.VoteRepository;
import com.challenge.poll.security.CustomUserDetails;
import com.challenge.poll.service.PollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(PollController.class);

    @GetMapping
    public List<PollResponse> getPolls() {
        return pollService.getAllPolls();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@RequestBody PollRequest pollRequest) {
        Poll poll = pollService.createPoll(pollRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Poll Created Successfully"));
    }

    @GetMapping("/{pollId}")
    public PollResponse getPollById(@PathVariable Long pollId) {
        return pollService.getPollById(pollId);
    }

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USER')")
    public PollResponse castVote(CustomUserDetails currentUser,
                                 @PathVariable Long pollId,
                                 @RequestBody VoteRequest voteRequest) {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
    }
}