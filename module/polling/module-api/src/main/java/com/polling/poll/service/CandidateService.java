package com.polling.poll.service;


import com.polling.poll.dto.comment.request.CommentDto;
import com.polling.poll.dto.candidate.request.ModifyCandidateRequestDto;
import com.polling.poll.dto.candidate.request.AddVoteCountRequestDto;
import com.polling.poll.dto.candidate.response.FindCandidateDetailsResponseDto;
import com.polling.entity.candidate.Candidate;
import com.polling.entity.candidate.CandidateHistory;
import com.polling.entity.member.Member;
import com.polling.entity.poll.status.PollStatus;
import com.polling.exception.CustomErrorResult;
import com.polling.exception.CustomException;
import com.polling.queryrepository.CandidateHistoryQueryRepository;
import com.polling.queryrepository.CommentQueryRepository;
import com.polling.repository.candidate.CandidateRepository;
import com.polling.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final CandidateHistoryQueryRepository candidateHistoryQueryRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public FindCandidateDetailsResponseDto getProfile(Long candidateId){
        Candidate candidate = getCandidate(candidateId);
        List<CommentDto> comments = commentQueryRepository.findAllByCandidateId(candidateId);
        return FindCandidateDetailsResponseDto.of(candidate, comments);
    }

    public void addVoteCount(AddVoteCountRequestDto requestDto, Long memberId){

        if(requestDto.getVoteCount() <= 0) throw new CustomException(CustomErrorResult.INVALID_VOTES);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorResult.USER_NOT_FOUND));

        Candidate candidate = getCandidate(requestDto.getCandidateId());

        Long id = candidate.getPoll().getId();

        if(candidateHistoryQueryRepository.existsByMemberIdAndPollIdInToday(memberId, id, LocalDate.now().atStartOfDay()))
            throw new CustomException(CustomErrorResult.ALREADY_VOTES);

        //todo : gRPC로 다른 api 호출 후 transaction id 받아오는 로직

        CandidateHistory.builder()
                .voteCount(requestDto.getVoteCount())
                .candidate(candidate)
                .member(member)
                .build();
    }

    public void modifyCandidate(Long candidateId, ModifyCandidateRequestDto requestDto) {
        Candidate candidate = getCandidate(candidateId);
        validateStatus(candidate.getPoll().getPollStatus());

        List<String> imagePaths = new ArrayList<>();
        imagePaths.add(requestDto.getImagePath1());
        imagePaths.add(requestDto.getImagePath2());
        imagePaths.add(requestDto.getImagePath3());
        if(requestDto.getName() != null)
            candidate.changeName(requestDto.getName());
        if(requestDto.getProfile() != null)
            candidate.changeProfile(requestDto.getProfile());
        if(requestDto.getThumbnail() != null)
            candidate.changeThumbnail(requestDto.getThumbnail());
        candidate.changeImagePaths(imagePaths);
    }

    public void deleteCandidate(Long candidateId) {
        if(candidateRepository.existsById(candidateId))
            throw  new CustomException(CustomErrorResult.CANDIDATE_NOT_FOUND);
        candidateRepository.deleteById(candidateId);
    }

    public Candidate getCandidate(Long candidateId){
        return candidateRepository
                .findById(candidateId).orElseThrow(() -> new CustomException(CustomErrorResult.CANDIDATE_NOT_FOUND));
    }

    public void validateStatus(PollStatus pollStatus){
        if(pollStatus == PollStatus.IN_PROGRESS || pollStatus == PollStatus.DONE)
            throw new CustomException(CustomErrorResult.IMPOSSIBLE_STATUS_TO_MODIFY);
    }

}