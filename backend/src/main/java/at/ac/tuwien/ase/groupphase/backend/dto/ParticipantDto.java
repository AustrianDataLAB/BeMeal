package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.Region;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String postalCode;

    @NotNull
    @NotEmpty
    private Map<Long, Integer> wins;

    @NotNull
    @NotEmpty
    private Region region;

    @NotNull
    @NotEmpty
    private LocalDateTime registered;

    @NotNull
    @NotEmpty
    private boolean isAdmin;
}
