package com.michalkowol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michalkowol.model.User;
import com.michalkowol.web.Page;
import com.softwareberg.Database;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.flywaydb.core.Flyway;
import spark.Request;
import spark.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.michalkowol.Randoms.randomEmail;
import static com.michalkowol.Randoms.randomPassword;
import static spark.Spark.get;

class App {

    private final DataSource dataSource = DataSourceSingleton.getInstance();
    private final Database db = new Database(dataSource);
    private final ObjectMapper objectMapper = new ObjectMapper();

    void run() {
        migrateDatabase();
        printToConsole();
        startWeb();
    }

    private void startWeb() {
        get("/users", this::getUsers, objectMapper::writeValueAsString);
        get("/users/:id", this::getUserById, objectMapper::writeValueAsString);
    }

    private Object getUserById(Request req, Response res) {
        res.type("application/json");
        int id = Integer.parseInt(req.params("id"));
        return getUserById(id);
    }

    private User getUserById(int id) {
        try (Connection connection = dataSource.getConnection()) {
            return User.findById(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Page<User> getUsers(Request req, Response res) {
        res.type("application/json");
        List<User> users = User.findAll(db);
        return new Page<>(users);
    }

    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    private List<User> getUsers() {
        try (Connection connection = dataSource.getConnection()) {
            return User.findAll(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void printToConsole() {
        try (Connection connection = dataSource.getConnection()) {
            User jan = User.findById(db, 1);
            if (jan != null) {
                jan.setPassword(randomPassword());
                jan.save(connection);
            }
            System.out.println(jan);

            User basia = User.of("basia", randomEmail(), "admin1");
            System.out.println(basia);
            basia.save(db);
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
