package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode
public class PlatformUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany
    private List<League> leagues;
}
