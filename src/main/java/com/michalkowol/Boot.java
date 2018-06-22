package com.michalkowol;

import com.michalkowol.model.User;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.michalkowol.Randoms.randomEmail;
import static com.michalkowol.Randoms.randomPassword;
import static spark.Spark.get;

class Boot {

    public static void main(String[] args) {
        App app = new App();
        app.run();




    }




}
