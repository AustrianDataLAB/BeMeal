package at.ac.tuwien.ase.groupphase.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.yaml")
public class Persistence {
    @Autowired
    Environment env;

    @Bean
    DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl(env.getProperty("datasource.url"));
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"); //TODO den path zum file ändren, schaut so aus als würde er das falsche yml verwenden und dann passt die url ned
        dataSource.setUsername(env.getProperty("datasource.user"));
        dataSource.setPassword(env.getProperty("datasource.password"));

        return dataSource;

//        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
//        dataSource.setUsername("admin");
//        dataSource.setPassword("password");
//
//        return dataSource;
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
