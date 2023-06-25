package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<PlatformUser, Long> {

    @Query("select count(*) > 0 from PlatformUser p where p.email = :email or p.username = :username")
    boolean exists(String email, String username);

    PlatformUser findByUsername(String username);

    @Query("select count(*) > 0 from PlatformUser p inner join p.ownerOf l where p.username = :username and l.id = :leagueId")
    boolean isCreatorOfLeague(String username, Long leagueId);

    PlatformUser findByEmail(String email);

    PlatformUser findByPasswordResetToken(UUID passwordResetToken);
}
