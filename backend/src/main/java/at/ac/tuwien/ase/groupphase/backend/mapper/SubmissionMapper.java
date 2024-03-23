package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.SubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.dto.WinningSubmissionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubmissionMapper {

    @Mapping(target = "picture", ignore = true)
    SubmissionDto submissionToSubmissionDto(Submission submission);

    @Mapping(target = "picture", ignore = true)
    Submission submissionDtoToSubmission(SubmissionDto submissionDto);

    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "participantName", ignore = true)
    @Mapping(target = "challengeId", ignore = true)
    WinningSubmissionDto submissionToWinningSubmissionDto(Submission submission);
}
