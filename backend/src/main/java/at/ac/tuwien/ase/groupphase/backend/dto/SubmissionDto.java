package at.ac.tuwien.ase.groupphase.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {

    @NotNull
    @NotEmpty
    private Long id;
    // base64
    private String picture;

    @NotNull
    @NotEmpty
    private LocalDateTime date;

}
