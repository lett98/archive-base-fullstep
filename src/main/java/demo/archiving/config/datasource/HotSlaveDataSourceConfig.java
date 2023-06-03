package demo.archiving.config.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HotSlaveDataSourceConfig {
    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "spring.hotslave.datasource")
    public DataSource slaveDataSource() {
        DataSource dataSource = DataSourceBuilder.create().build();
        return dataSource;
    }
}
