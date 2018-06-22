package com.michalkowol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michalkowol.model.User;
import org.flywaydb.core.Flyway;
import spark.Request;
import spark.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.michalkowol.Randoms.randomEmail;
import static com.michalkowol.Randoms.randomPassword;
import static spark.Spark.get;

class App {

    private final DataSource dataSource = DataSourceSingleton.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void run() {
        migrateDatabase();
        printToConsole();
        startWeb();
    }

    private void startWeb() {
        get("/users/:id", this::getUserById, objectMapper::writeValueAsString);
    }

    private Object getUserById(Request req, Response res) {
        int id = Integer.parseInt(req.params("id"));
        User user = getUser(id);
        res.type("application/json");
        return user;
    }

    private User getUser(int id) {
        try (Connection connection = dataSource.getConnection()) {
            return User.findById(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void printToConsole() {
        try (Connection connection = dataSource.getConnection()) {
            User jan = User.findById(connection, 1);
            if (jan != null) {
                jan.setPassword(randomPassword());
                jan.save(connection);
            }
            System.out.println(jan);

            User basia = User.of("basia", randomEmail(), "admin1");
            System.out.println(basia);
            basia.save(connection);
            System.out.println(basia);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void migrateDatabase() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
}
