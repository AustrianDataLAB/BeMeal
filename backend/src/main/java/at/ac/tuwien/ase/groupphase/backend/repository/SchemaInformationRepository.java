package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.SchemaInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SchemaInformationRepository extends JpaRepository<SchemaInformation, Long> {

    @Query("select s from SchemaInformation s where s.id = 1")
    Optional<SchemaInformation> getSchemaInformation();
}
