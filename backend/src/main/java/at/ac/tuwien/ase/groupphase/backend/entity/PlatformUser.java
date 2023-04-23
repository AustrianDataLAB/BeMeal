package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(fetch = FetchType.EAGER)
    private List<League> ownerOf;
}
