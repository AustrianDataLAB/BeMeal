package at.ac.tuwien.ase.groupphase.backend.datagenerator;

import at.ac.tuwien.ase.groupphase.backend.service.CommunityIdentificationService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile("SmallDataGenerator")
public class SmallDataGenerator {
    private final Logger logger = LoggerFactory.getLogger(SmallDataGenerator.class);
    private final DataSource source;

    @Autowired
    private DataGeneratorHealthIndicator healthIndicator;

    @Autowired
    private CommunityIdentificationService communityIdentificationService;

    public SmallDataGenerator(@Qualifier("rdbmsDataSource") DataSource source) {
        this.source = source;
    }

    @PostConstruct
    @Transactional("rdbmsTxManager")
    void insertDummyData() {
        communityIdentificationService.reloadCommunityIdentifications();
        try (Connection c = source.getConnection()) {
            ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/DefaultDataGen.sql"));
            logger.info("SmallDataGenerator finished");
            // ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/SmallDataGen.sql"));

            healthIndicator.setReady();
        } catch (SQLException sqle) {
            logger.error("An error occurred whilst trying to insert testdata", sqle);
        }
    }
}
