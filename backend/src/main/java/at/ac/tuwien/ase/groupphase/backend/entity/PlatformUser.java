package at.ac.tuwien.ase.groupphase.backend.entity;

import at.ac.tuwien.ase.groupphase.backend.entity.generator.ExplicitIdGenerator;
import at.ac.tuwien.ase.groupphase.backend.entity.generator.ExplicitIdSequence;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PlatformUser {
    @Id
    @ExplicitIdSequence(name = "plt_user_seq")
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private byte[] password;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private Boolean isAdmin;

    @Column(unique = true)
    private UUID passwordResetToken;

    @OneToMany(fetch = FetchType.EAGER)
    private List<League> ownerOf;
}
