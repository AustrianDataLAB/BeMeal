package at.ac.tuwien.ase.groupphase.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinningSubmissionDto extends SubmissionDto {

    private String participantName;

    private Long challengeId;
}
