package at.ac.tuwien.ase.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class SchemaInformation {

    @Id
    private Long id;

    private boolean initialized;

    private String generator;

    public SchemaInformation(final String generator) {
        this.id = 1L;
        this.initialized = true;
        this.generator = generator;
    }
}
