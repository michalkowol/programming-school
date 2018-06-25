package com.michalkowol.model;

import com.softwareberg.Database;
import com.softwareberg.SqlStatement;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
@Getter
@ToString
public class User {

    private int id;

    @Setter
    private String username;

    @Setter
    private String email;

    private String password;

    private User(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void save(Connection connection) throws SQLException {
        if (id == 0) {
            insert(connection);
        } else {
            update(connection);
        }
    }

    public void save(Database db) {
        if (id == 0) {
            insert(db);
        } else {
            update(db);
        }
    }

    private void insert(Connection connection) throws SQLException {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        PreparedStatement sql = connection.prepareStatement(query, new String[]{"id"}); // RETURN_GENERATED_KEYS https://stackoverflow.com/a/30555032/2051256
        sql.setString(1, username);
        sql.setString(2, email);
        sql.setString(3, password);
        sql.executeUpdate();
        ResultSet rs = sql.getGeneratedKeys();
        if (rs.next()) {
            id = rs.getInt(1);
        }
    }

    private void insert(Database db) {
        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        SqlStatement sql = SqlStatement.create(query, username, email, password);
        id = (Integer) db.insert(sql).get("id");
    }

    private void update(Connection connection) throws SQLException {
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        PreparedStatement sql = connection.prepareStatement(query);
        sql.setString(1, username);
        sql.setString(2, email);
        sql.setString(3, password);
        sql.setInt(4, id);
        sql.executeUpdate();
    }

    private void update(Database db) {
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        SqlStatement sql = SqlStatement.create(query, username, email, password, id);
        db.update(sql);
    }

    public static User findById(Connection connection, int id) throws SQLException {
        String query = "SELECT username, email, password FROM users WHERE id = ?";
        PreparedStatement sql = connection.prepareStatement(query);
        sql.setInt(1, id);
        ResultSet rs = sql.executeQuery();
        if (rs.next()) {
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            return new User(id, username, email, password);
        } else {
            return null;
        }
    }

    public static User findById(Database db, int id) {
        return db.findOne("SELECT id, username, email, password FROM users", row -> new User(
            row.intValue("id"),
            row.string("username"),
            row.string("email"),
            row.string("password")
        ));
    }

    public static List<User> findAll(Connection connection) throws SQLException {
        String query = "SELECT id, username, email, password FROM users";
        PreparedStatement sql = connection.prepareStatement(query);
        ResultSet rs = sql.executeQuery();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String password = rs.getString("password");
            users.add(new User(id, username, email, password));
        }
        return users;
    }

    public static List<User> findAll(Database db) {
        return db.findAll("SELECT id, username, email, password FROM users", row -> new User(
            row.intValue("id"),
            row.string("username"),
            row.string("email"),
            row.string("password")
        ));
    }

    public static User of(String username, String email, String password) {
        return new User(0, username, email, hashPassword(password));
    }
}
