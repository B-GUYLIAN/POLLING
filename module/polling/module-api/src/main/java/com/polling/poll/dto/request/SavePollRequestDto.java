package com.polling.poll.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.polling.entity.poll.Poll;
import com.polling.poll.dto.candidate.request.SaveCandidateRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SavePollRequestDto {

  List<SaveCandidateRequestDto> candidateDtos;
  @NotNull
  String title;
  @NotNull
  String content;
  @NotNull
  String thumbnail;
  @NotNull
  Boolean openStatus;
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
  LocalDateTime startDate;
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
  LocalDateTime endDate;

  public Poll toPollEntity() {
    return Poll.builder()
        .title(title)
        .content(content)
        .thumbnail(thumbnail)
        .openStatus(openStatus)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }
}