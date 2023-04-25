package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("Recipe")
@Data
@EqualsAndHashCode
public class Recipe {
    @Id
    @Property("id")
    private final String recipeId;
    @Property("name")
    private final String name;
    @Property("preparationTime")
    private final Integer preparationTime;
    @Property("cookingTime")
    private final Integer cookingTime;
    @Property("description")
    private final String description;
    @Property("skillLevel")
    private final String skillLevel;

    // TODO: how to solve the amount of ingredients issue?
    @Relationship(type = "CONTAINS_INGREDIENT", direction = Relationship.Direction.OUTGOING)
    private List<Ingredient> ingredients;
}
