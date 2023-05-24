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

    @Query(value = "MATCH (r:Recipe) WHERE r.name =~ '(?i).*' + $searchString + '.*' RETURN r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe) WHERE r.name =~ '(?i).*' + $searchString + '.*' RETURN COUNT(r)")
    Page<Recipe> findRecipesBySearchString(String searchString, Pageable pageable);
}
