package at.ac.tuwien.ase.groupphase.backend.entity.generator;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(ExplicitIdGenerator.class)
@Retention(RUNTIME)
@Target({ METHOD, FIELD })
public @interface ExplicitIdSequence {
    String name();

    int startWith() default 100;

    int incrementBy() default 50;
}
