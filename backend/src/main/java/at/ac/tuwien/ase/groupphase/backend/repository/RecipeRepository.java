package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends Neo4jRepository<Recipe, String> {
    List<Recipe> findByNameIsLike(@Param("name") String name);

    @Query("MATCH (r:Recipe) RETURN r ORDER BY rand() LIMIT 1")
    Optional<Recipe> findAnyRecipe();

    @Query("MATCH (r:Recipe) WHERE r.picture IS NOT NULL RETURN r ORDER BY rand() LIMIT 1") // todo: handle picture uuid
                                                                                            // attribute
    Optional<Recipe> findAnyRecipeWithPicture();

    @Query("MATCH (r:Recipe) \n" + "WHERE r.picture IS NOT NULL \n" + "WITH r \n" + "ORDER BY rand() \n"
            + "LIMIT $amount\n" + "OPTIONAL MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)\n"
            + "RETURN r, collect(c), collect(i)")
    Optional<List<Recipe>> findAnyRecipeWithPictureWithIngredients(int amount);

    @Query(value = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN COUNT(r)")
    Page<Recipe> getRecipesFromCollection(List<String> names, Pageable pageable);

    @Query("MATCH (r:Recipe)\n" + "WHERE r.id in $rec_ids\n" + "WITH r\n"
            + "OPTIONAL MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)\n" + "RETURN r, collect(c), collect(i)")
    Optional<List<Recipe>> findMutlipleRecipesWithId(List<String> rec_ids);

    @Query("MATCH (r:Recipe)\n" + "WITH count {\n" + "    MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)\n"
            + "    WHERE size([x IN $names WHERE i.name =~ x])>0\n"
            + "} AS wanted, count{MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)} AS total, r\n"
            + "WHERE wanted > 0.6 * total\n" + "OPTIONAL MATCH (r)-[a:CONTAINS_INGREDIENT]->(b:Ingredient)\n"
            + "RETURN r, collect(a), collect(b), (wanted*1.0/total), wanted, total\n"
            + "ORDER BY (wanted*1.0/total) DESC\n" + "LIMIT 100")
    Optional<List<Recipe>> getSimilarMeals(List<String> names);
}
