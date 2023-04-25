package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends Neo4jRepository<Recipe, String> {
    List<Recipe> findByNameIsLike(@Param("name") String name);

    @Query("MATCH (r:Recipe) RETURN r SKIP rand() * CALL {RETURN count(*)}")
    Optional<Recipe> findAnyRecipe();

    @Query("MATCH (r:Recipe) RETURN r SKIP rand() * CALL {RETURN count(*)}") // todo: handle picture uuid attribute
    Optional<Recipe> findAnyRecipeWithPicture();
}
