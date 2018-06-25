package com.michalkowol;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public final class Configuration {

    private static final Config CONFIG = ConfigFactory.load();

    private Configuration() {
    }

    public static final class Server {
        public static int port() {
            return CONFIG.getInt("server.port");
        }
    }

    public static final class Datasource {
        public static String jdbcUrl() {
            return CONFIG.getString("datasource.jdbcUrl");
        }

        public static String username() {
            return CONFIG.getString("datasource.username");
        }

        public static String password() {
            return CONFIG.getString("datasource.password");
        }
    }
}
