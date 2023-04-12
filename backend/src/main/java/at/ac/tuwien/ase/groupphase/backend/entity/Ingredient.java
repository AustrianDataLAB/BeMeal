package at.ac.tuwien.ase.groupphase.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("Ingredient")
@Data
@EqualsAndHashCode
public class Ingredient {
    @Id
    @GeneratedValue
    private final UUID id;
    private final String name;
}
