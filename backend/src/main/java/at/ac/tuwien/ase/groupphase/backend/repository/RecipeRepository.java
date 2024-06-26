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

    @Query("MATCH (r:Recipe) WHERE r.picture IS NOT NULL RETURN r ORDER BY rand() LIMIT 1")
    Optional<Recipe> findAnyRecipeWithPicture();

    @Query("""
            MATCH (r:Recipe)\s
            WHERE r.picture IS NOT NULL\s
            WITH r\s
            ORDER BY rand()\s
            LIMIT $amount
            OPTIONAL MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)
            RETURN r, collect(c), collect(i)""")
    Optional<List<Recipe>> findAnyRecipeWithPictureWithIngredients(int amount);

    @Query(value = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe)-[:COLLECTION]->(c:Collection) WHERE c.name IN $names RETURN COUNT(r)")
    Page<Recipe> getRecipesFromCollection(List<String> names, Pageable pageable);

    @Query(value = "MATCH (r:Recipe)-[:DIET_TYPE]->(d:DietType) WHERE r.name =~ '(?i).*' + $searchString + '.*' "
            + "AND CASE WHEN $maxTime IS NOT NULL THEN r.cookingTime + r.preparationTime <= $maxTime ELSE TRUE END "
            + "AND CASE WHEN $skillLevels IS NOT NULL THEN r.skillLevel IN $skillLevels ELSE TRUE END "
            + "AND CASE WHEN $dietTypes IS NOT NULL THEN d.name IN $dietTypes ELSE TRUE END "
            + "RETURN DISTINCT r SKIP $skip LIMIT $limit", countQuery = "MATCH (r:Recipe)-[:DIET_TYPE]->(d:DietType) WHERE r.name =~ '(?i).*' + $searchString + '.*' "
                    + "AND CASE WHEN $maxTime IS NOT NULL THEN r.cookingTime + r.preparationTime <= $maxTime ELSE TRUE END "
                    + "AND CASE WHEN $skillLevels IS NOT NULL THEN r.skillLevel IN $skillLevels ELSE TRUE END "
                    + "AND CASE WHEN $dietTypes IS NOT NULL THEN d.name IN $dietTypes ELSE TRUE END "
                    + "RETURN COUNT(DISTINCT r)")
    Page<Recipe> findRecipesBySearchString(String searchString, List<String> skillLevels, Integer maxTime,
            List<String> dietTypes, Pageable pageable);

    @Query("MATCH (r:Recipe)\n" + "WHERE r.id in $rec_ids\n" + "WITH r\n"
            + "OPTIONAL MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)\n" + "RETURN r, collect(c), collect(i)")
    Optional<List<Recipe>> findMutlipleRecipesWithId(List<String> rec_ids);

    @Query("""
            MATCH (r:Recipe)
            WITH count {
                MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)
                WHERE size([x IN $names WHERE i.name =~ x])>0
            } AS wanted, count{MATCH (r)-[c:CONTAINS_INGREDIENT]->(i:Ingredient)} AS total, r
            WHERE wanted > 0.6 * total AND NOT r.id IN  $notWanted
            OPTIONAL MATCH (r)-[a:CONTAINS_INGREDIENT]->(b:Ingredient)
            RETURN r, collect(a), collect(b), (wanted*1.0/total), wanted, total
            ORDER BY (wanted*1.0/total) DESC
            LIMIT 100""")
    Optional<List<Recipe>> getSimilarMeals(List<String> names, List<String> notWanted);
}
