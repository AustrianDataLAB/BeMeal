package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends Neo4jRepository<Recipe, String> {
    @Query("MATCH (r:Recipe) RETURN r ORDER BY rand() LIMIT 1")
    Optional<Recipe> findAnyRecipe();

    @Query("MATCH (r:Recipe) WHERE r.picture IS NOT NULL RETURN r ORDER BY rand() LIMIT 1") // todo: handle picture uuid
                                                                                            // attribute
    Optional<Recipe> findAnyRecipeWithPicture();

    @Query(value = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN COUNT(r)")
    Page<Recipe> getRecipesFromCollection(List<String> names, Pageable pageable);

    @Query(value = "MATCH (r:Recipe)-[:DIET_TYPE]->(d:DietType) WHERE r.name =~ '(?i).*' + $searchString + '.*' "
            + "AND CASE WHEN $maxTime IS NOT NULL THEN r.cookingTime + r.preparationTime <= $maxTime ELSE TRUE END "
            + "AND CASE WHEN $skillLevel IS NOT NULL THEN r.skillLevel = $skillLevel ELSE TRUE END "
            + "AND CASE WHEN $dietTypes IS NOT NULL THEN d.name IN $dietTypes ELSE TRUE END "
            + "RETURN r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe)-[:DIET_TYPE]->(d:DietType) WHERE r.name =~ '(?i).*' + $searchString + '.*' "
                    + "AND CASE WHEN $maxTime IS NOT NULL THEN r.cookingTime + r.preparationTime <= $maxTime ELSE TRUE END "
                    + "AND CASE WHEN $skillLevel IS NOT NULL THEN r.skillLevel = $skillLevel ELSE TRUE END "
                    + "AND CASE WHEN $dietTypes IS NOT NULL THEN d.name IN $dietTypes ELSE TRUE END "
                    + "RETURN COUNT(r)")
    Page<Recipe> findRecipesBySearchString(String searchString, String skillLevel, Integer maxTime,
            List<String> dietTypes, Pageable pageable);
}
