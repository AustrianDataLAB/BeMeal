package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.RecipeCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface RecipeCollectionRepository extends Neo4jRepository<RecipeCollection, String> {
    @Query("MATCH (c:Collection) RETURN c ORDER BY rand() LIMIT 8")
    List<RecipeCollection> getRandomizedRecipeCollectionSelection();

    @Query(value = "MATCH (c:Collection) WHERE c.name =~ '(?i).*' + $searchString + '.*' RETURN c SKIP $skip LIMIT $limit", countQuery = "MATCH (c:Collection) WHERE c.name =~ '(?i).*' + $searchString + '.*' RETURN COUNT(c)")
    Page<RecipeCollection> findRecipeCollectionsBySearchString(String searchString, Pageable pageable);
}
