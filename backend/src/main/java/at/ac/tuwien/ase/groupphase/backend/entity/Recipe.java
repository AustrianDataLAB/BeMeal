package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.UUID;

@Node("Recipe")
@Data
@EqualsAndHashCode
public class Recipe {
    @Id
    @GeneratedValue
    private final UUID id;
    private final String name;
    private final UUID picture;

    //TODO: how to solve the amount of ingredients issue?
    @Relationship(type = "CONTAINS", direction = Relationship.Direction.OUTGOING)
    private List<Ingredient> ingredients;
}
