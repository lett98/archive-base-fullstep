package demo.archiving.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "insertEntityManagerFactory",
        transactionManagerRef = "insertTransactionManager",
        basePackages = {"demo.archiving.repository.entity.arc"}
)

public class ArchiveDataSourceConfig {

    private String MODEL_PACKAGE = "demo.archiving.model.entity.arc";

    @Bean(name = "archiveDataSource")
    @ConfigurationProperties(prefix = "spring.archive.datasource")
    public DataSource archiveDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean(name = "insertEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        return entityManagerFactoryBuilder
                .dataSource(archiveDataSource())
                .packages(MODEL_PACKAGE)
                .build();
    }

    @Bean(name = "insertTransactionManager")
    public PlatformTransactionManager archiveTransactionManager(@Qualifier("insertEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
