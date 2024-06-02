package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeCollectionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeCollection;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeCollectionMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeCollectionService {
    private final Logger logger = LoggerFactory.getLogger(RecipeCollectionService.class);
    private final RecipeCollectionRepository collectionRepository;
    private final RecipeCollectionMapper collectionMapper = new RecipeCollectionMapper();

    @Transactional("neo4jTxManager")
    public List<RecipeCollectionDto> getRandomizedRecipeCollectionSelection() {
        logger.trace("Getting all collections");
        List<RecipeCollection> collections = collectionRepository.getRandomizedRecipeCollectionSelection();
        if (collections.isEmpty()) {
            throw new NoSuchElementException("No recipe collections found");
        }
        return collections.stream().map(collectionMapper::collectionToCollectionDto).collect(Collectors.toList());
    }

    @Transactional("neo4jTxManager")
    public Page<RecipeCollectionDto> findRecipeCollectionsBySearchString(String searchString, int page, int size) {
        logger.trace("Searching for collections which contain the string: " + searchString);
        var collections = collectionRepository
                .findRecipeCollectionsBySearchString(searchString, PageRequest.of(page, size))
                .map(collectionMapper::collectionToCollectionDto);
        if (collections.isEmpty()) {
            throw new NoSuchElementException("No recipe collections found");
        }
        return collections;
    }
}
