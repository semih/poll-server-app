package com.challenge.poll.repository;

import com.challenge.poll.model.jpa.Poll;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findById(Long pollId);

    List<Poll> findByIdIn(List<Long> pollIds);

    List<Poll> findByIdIn(List<Long> pollIds, Sort sort);
}