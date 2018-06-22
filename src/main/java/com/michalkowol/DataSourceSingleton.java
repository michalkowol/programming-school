package com.michalkowol;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceSingleton {

    private final DataSource dataSource;

    private DataSourceSingleton(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static DataSource getInstance() {
        return SingletonHelper.INSTANCE.dataSource;
    }

    private static class SingletonHelper {

        private static final DataSourceSingleton INSTANCE = createDatabaseSingleton();

        private static DataSourceSingleton createDatabaseSingleton() {
            Config config = ConfigFactory.load();
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(config.getString("datasource.jdbcUrl"));
            hikariConfig.setUsername(config.getString("datasource.username"));
            hikariConfig.setPassword(config.getString("datasource.password"));
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            return new DataSourceSingleton(dataSource);
        }
    }
}
