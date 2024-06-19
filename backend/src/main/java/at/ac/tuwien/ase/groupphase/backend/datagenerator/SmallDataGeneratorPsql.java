package at.ac.tuwien.ase.groupphase.backend.datagenerator;

import at.ac.tuwien.ase.groupphase.backend.repository.SchemaInformationRepository;
import at.ac.tuwien.ase.groupphase.backend.service.CommunityIdentificationService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile("SmallDataGeneratorPsql")
public class SmallDataGeneratorPsql {
    private final Logger logger = LoggerFactory.getLogger(SmallDataGeneratorPsql.class);
    private final DataSource source;

    @Autowired
    private CommunityIdentificationService communityIdentificationService;

    @Autowired
    private SchemaInformationRepository schemaInformationRepository;

    public SmallDataGeneratorPsql(DataSource source) {
        this.source = source;
    }

    @PostConstruct
    @Transactional("rdbmsTxManager")
    void insertDummyData() {
        final var schemaInformationOpt = this.schemaInformationRepository.getSchemaInformation();

        if (schemaInformationOpt.isPresent()) {
            final var si = schemaInformationOpt.get();
            if (si.isInitialized()) {
                this.logger.info("Schema data is already initialized by {}, skip initialization", si.getGenerator());
                return;
            }
        }

        communityIdentificationService.reloadCommunityIdentifications();
        try (Connection c = source.getConnection(); final var ps = c.prepareStatement("INSERT INTO SCHEMA_INFO (ID, " +
                "INITIALIZED, GENERATOR) VALUES (?,?,?)")) {
            c.setAutoCommit(false);

            ps.setLong(1, 1);
            ps.setBoolean(2, true);
            ps.setString(3, this.getClass().getSimpleName());
            ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/DefaultDataGenPsql.sql"));
            ps.execute();

            c.commit();
        } catch (SQLException sqle) {
            logger.error("An error occurred whilst trying to insert testdata", sqle);
        }
    }
}
