package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("DietType")
@Data
@EqualsAndHashCode
public class RecipeDietType {
    @Id
    @Property("name")
    private final String name;
}
