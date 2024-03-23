package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
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
