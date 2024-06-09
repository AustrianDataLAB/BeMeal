package at.ac.tuwien.ase.groupphase.backend.configuration;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "at.ac.tuwien.ase.groupphase.backend.repository", transactionManagerRef = "neo4jTxManager")
@EnableTransactionManagement
public class Neo4JConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    @Bean
    Driver getConfiguration() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    @Bean("neo4jTxManager")
    public Neo4jTransactionManager neo4jTransactionManager() {
        return new Neo4jTransactionManager(getConfiguration());
    }

    @Bean
    org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return org.neo4j.cypherdsl.core.renderer.Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
    }
}
