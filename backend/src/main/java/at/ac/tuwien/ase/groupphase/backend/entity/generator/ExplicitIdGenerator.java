package at.ac.tuwien.ase.groupphase.backend.entity.generator;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;

public class ExplicitIdGenerator extends SequenceStyleGenerator {

    private final ExplicitIdSequence config;

    public ExplicitIdGenerator(ExplicitIdSequence config) {
        super();
        this.config = config;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Object id = session.getEntityPersister(null, object).getIdentifier(object, session);
        return id != null ? id : super.generate(session, object);
    }

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) throws MappingException {
        parameters.setProperty("initial_value", this.config.startWith() + "");
        parameters.setProperty("increment_by", this.config.incrementBy() + "");
        parameters.setProperty("sequence_name", this.config.name());
        super.configure(type, parameters, serviceRegistry);
    }
}
