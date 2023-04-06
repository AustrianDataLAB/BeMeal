package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.Region;
import lombok.Data;
import lombok.EqualsAndHashCode;

public record Registration(String email, String username, String password, Region region, String postalCode) {

}
