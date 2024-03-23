package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeCollectionDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeCollectionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<RecipeCollectionDto>> findRecipeCollectionsBySearchString(
            @RequestParam(value = "name") final String name,
            @RequestParam(required = false, defaultValue = "0", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "25", value = "size") Integer size) {
        logger.trace("GET /api/v1/recipe/recipeCollection/search?name={}", name);
        Page<RecipeCollectionDto> dto = collectionService.findRecipeCollectionsBySearchString(name, page, size);
        if (dto == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok(dto);
    }
}
