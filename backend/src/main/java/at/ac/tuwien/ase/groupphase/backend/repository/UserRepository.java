package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<PlatformUser, Long> {

    @Query("select count(*) > 0 from PlatformUser p where p.email = :email or p.username = :username")
    boolean exists(String email, String username);
}
