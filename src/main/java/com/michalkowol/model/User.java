package com.michalkowol.model;

import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
public class User {

    private int id;
    private String username;
    private String email;
    private String password;

    private User(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
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

    private void update(Connection connection) throws SQLException {
        String query = "UPDATE users SET username=?, email=?, password=? WHERE id = ?";
        PreparedStatement sql = connection.prepareStatement(query);
        sql.setString(1, username);
        sql.setString(2, email);
        sql.setString(3, password);
        sql.setInt(4, id);
        sql.executeUpdate();
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

    public static User of(String username, String email, String password) {
        return new User(0, username, email, hashPassword(password));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("username", username)
            .add("email", email)
            .add("password", password)
            .toString();
    }
}
