package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.CommunityIdentification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityIdentificationRepository extends JpaRepository<CommunityIdentification, Long> {
}
