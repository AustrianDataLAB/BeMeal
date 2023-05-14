package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommunityIdentification {

    @Id
    private Long communityIdentificationNumber;

    @Column(unique = true)
    private String postalCode;

    @OneToMany(mappedBy = "communityIdentification")
    private Set<Participant> participants;

}
