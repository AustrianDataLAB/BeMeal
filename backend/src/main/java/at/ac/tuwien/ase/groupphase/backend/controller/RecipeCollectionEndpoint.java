package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeCollectionDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeCollectionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recipeCollection")
public class RecipeCollectionEndpoint {
    private final Logger logger = LoggerFactory.getLogger(RecipeCollectionEndpoint.class);
    private final RecipeCollectionService collectionService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<RecipeCollectionDto>> getRandomizedRecipeCollectionSelection() {
        logger.trace("GET /api/v1/recipeCollection");

        List<RecipeCollectionDto> recipeCollectionDtoList = collectionService.getRandomizedRecipeCollectionSelection();

        if (recipeCollectionDtoList == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(recipeCollectionDtoList);
    }
}
