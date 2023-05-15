package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeCollectionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeCollection;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeCollectionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeCollectionRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecipeCollectionService {
    private final Logger logger = LoggerFactory.getLogger(RecipeCollectionService.class);
    private final RecipeCollectionRepository collectionRepository;
    private final RecipeCollectionMapper collectionMapper;

    @Autowired
    @NotNull
    public RecipeCollectionService(RecipeCollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
        this.collectionMapper = new RecipeCollectionMapper();
    }

    public List<RecipeCollectionDto> getAllCollections() {
        logger.trace("Getting all collections");
        List<RecipeCollection> collections = collectionRepository.findAll();
        if (collections.isEmpty()) {
            throw new NoSuchElementException("No recipe collections found");
        }
        return collections.stream().map(collectionMapper::collectionToCollectionDto).collect(Collectors.toList());
    }

}
