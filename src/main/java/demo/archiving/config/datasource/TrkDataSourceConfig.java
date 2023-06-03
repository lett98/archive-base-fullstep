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
        entityManagerFactoryRef = "trkCMEntityManagerFactory",
        transactionManagerRef = "trkCMTransactionManager",
        basePackages = {"demo.archiving.repository.trk"}
)
public class TrkDataSourceConfig {
    private String MODEL_PACKAGE = "demo.archiving.model.trk";

    @Bean("trkDataSource")
    @ConfigurationProperties(prefix = "spring.trk.datasource")
    public DataSource trkDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean(name = "trkCMEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean trkEntityManagerFactory(EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        return entityManagerFactoryBuilder
                .dataSource(trkDataSource())
                .packages(MODEL_PACKAGE)
                .build();
    }

    @Bean(name = "trkCMTransactionManager")
    public PlatformTransactionManager trkTransactionManager(@Qualifier("trkCMEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
