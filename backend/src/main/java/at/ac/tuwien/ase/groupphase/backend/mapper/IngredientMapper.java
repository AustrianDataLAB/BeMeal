package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;

// ToDo: Adjust mapper
public class IngredientMapper {

    public IngredientDto recordToIngredientDto(Value value) {
        var node = value.get("ingredient");
        // return new IngredientDto(node.get("identity").asLong(), node.get("name").asString());
        return new IngredientDto(node.get("name").asString());
    }

    public IngredientDto recordToIngredientDto(Record record) {
        var node = record.get("ingredient");
        // return new IngredientDto(node.get("identity").asLong(), node.get("name").asString());
        return new IngredientDto(node.get("name").asString());
    }
}
