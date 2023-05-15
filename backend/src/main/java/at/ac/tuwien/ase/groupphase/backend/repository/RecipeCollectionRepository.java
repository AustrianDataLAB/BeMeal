package at.ac.tuwien.ase.groupphase.backend.repository;

import at.ac.tuwien.ase.groupphase.backend.entity.RecipeCollection;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface RecipeCollectionRepository extends Neo4jRepository<RecipeCollection, String> {

}
