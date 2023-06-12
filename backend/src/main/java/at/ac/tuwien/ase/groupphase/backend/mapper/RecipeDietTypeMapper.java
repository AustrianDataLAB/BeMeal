package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDietTypeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeDietType;

public class RecipeDietTypeMapper {

    public RecipeDietTypeDto dietTypeToDietTypeDto(RecipeDietType dietType) {
        return new RecipeDietTypeDto(dietType.getName());
    }
}
