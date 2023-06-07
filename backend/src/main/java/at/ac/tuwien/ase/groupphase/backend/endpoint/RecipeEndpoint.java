package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/api/v1/recipe")
public class RecipeEndpoint {

    private final Logger logger = LoggerFactory.getLogger(RecipeEndpoint.class);
    private final RecipeService recipeService;

    @GetMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RecipeDto> getRecipeById(@NotNull @PathVariable final String recipeId) {
        logger.trace("GET /api/v1/recipe/{}", recipeId);

        RecipeDto dto = recipeService.getRecipeById(recipeId);

        if (dto == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<RecipeDto>> findRecipeCollectionsBySearchString(
            @RequestParam(value = "name") final String name,
            @RequestParam(required = false, value = "skillLevel") final String skillLevel,
            @RequestParam(required = false, value = "maxTime") final Integer maxTime,
            @RequestParam(required = false, value = "dietType") final List<String> dietTypes,
            @RequestParam(required = false, defaultValue = "0", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "25", value = "size") Integer size) {
        logger.trace("GET /api/v1/recipe/recipe/search?name={},skillLevel={},maxTime={},dietType={}", name, skillLevel,
                maxTime, dietTypes);
        Page<RecipeDto> dto = recipeService.findRecipesBySearchString(name, skillLevel, maxTime, dietTypes, page, size);
        if (dto == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/collections")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<RecipeDto>> getRecipesFromCollection(
            @RequestParam(value = "name") final List<String> names,
            @RequestParam(required = false, defaultValue = "0", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "25", value = "size") Integer size) {
        logger.trace("GET /api/v1/recipe/collections?names.size={}", names.size());
        Page<RecipeDto> dto = recipeService.getRecipesFromCollections(names, page, size);
        if (dto == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/suggestion")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeDto> getSuggestions(@RequestParam(value = "id") final List<String> ids) {
        logger.info("GET /api/v1/recipe/suggestions?ids.size={}", ids.size());
        return this.recipeService.getSuggestions(ids);
    }
}
