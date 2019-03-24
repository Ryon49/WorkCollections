package com.wdong.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties("fablix.datasource.master")
    public DataSourceProperties masterProperties() {
        return new DataSourceProperties();
    }

    @Bean(name="master_datasource")
    public HikariDataSource masterDataSource() {
        return masterProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }
}
