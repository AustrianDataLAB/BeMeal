package at.ac.tuwien.ase.groupphase.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinningSubmissionDto extends SubmissionDto {

    private String participantName;

    private Long challengeId;
}
