package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Recipe")
@Data
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
    @Property("picture")
    private final String pictureUUID;

    @Relationship(type = "CONTAINS_INGREDIENT", direction = Relationship.Direction.OUTGOING)
    private List<Ingredient> ingredients;

    @Relationship(type = "COLLECTION", direction = Relationship.Direction.OUTGOING)
    private List<RecipeCollection> collections;

    @Relationship(type = "DIET_TYPE", direction = Relationship.Direction.OUTGOING)
    private List<RecipeDietType> dietTypes;
}
