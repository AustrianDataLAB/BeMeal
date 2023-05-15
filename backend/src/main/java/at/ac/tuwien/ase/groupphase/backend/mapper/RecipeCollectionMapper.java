package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeCollectionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeCollection;

public class RecipeCollectionMapper {
    public RecipeCollectionDto collectionToCollectionDto(RecipeCollection collection) {
        return new RecipeCollectionDto(collection.getName());
    }
}
