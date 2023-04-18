package at.ac.tuwien.ase.groupphase.backend.datagenerator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile("SmallDataGenerator")
public class SmallDataGenerator {
	private final Logger logger = LoggerFactory.getLogger(SmallDataGenerator.class);
	private final DataSource source;

	public SmallDataGenerator(DataSource source) {
		this.source = source;
	}

	@PostConstruct
	void insertDummyData() {
		try (Connection c = source.getConnection()) {
			ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/DefaultDataGen.sql"));
			ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/SmallDataGen.sql"));
		} catch (SQLException sqle) {
			logger.error("An error occurred whilst trying to insert testdata", sqle);
		}
	}
}
