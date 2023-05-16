package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ResponseEntity<List<RecipeDto>> searchRecipeByName(@RequestParam(value = "name") final String name) {
        logger.trace("GET /api/v1/recipe/search?name={}", name);

        List<RecipeDto> dto = recipeService.searchRecipeByName(name);

        if (dto == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(dto);
    }

}
