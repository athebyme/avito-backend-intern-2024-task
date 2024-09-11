package io.codefresh.gradleexample.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {
    @Value("${POSTGRES_JDBC_URL}")
    private String jdbcUrl;

    @Value("${POSTGRES_USERNAME}")
    private String username;

    @Value("${POSTGRES_PASSWORD}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
