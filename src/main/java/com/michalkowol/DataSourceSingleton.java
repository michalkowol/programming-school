package com.michalkowol;

import com.michalkowol.Configuration.Datasource;
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
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(Datasource.jdbcUrl());
            hikariConfig.setUsername(Datasource.username());
            hikariConfig.setPassword(Datasource.password());
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            return new DataSourceSingleton(dataSource);
        }
    }
}
