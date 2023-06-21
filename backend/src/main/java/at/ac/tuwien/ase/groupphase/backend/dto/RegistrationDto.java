package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.Region;

public record RegistrationDto(String email, String username, String password, Region region, String postalCode) {

}
