package io.yfam.yagily.dao;

import io.yfam.yagily.dao.exceptions.CouldNotRetrieveGeneratedIdException;
import io.yfam.yagily.dao.exceptions.DeleteFailedException;
import io.yfam.yagily.dao.exceptions.InsertFailedException;
import io.yfam.yagily.dao.exceptions.UpdateFailedException;
import io.yfam.yagily.dto.Permission;
import io.yfam.yagily.dto.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.yfam.yagily.dao.utils.DaoUtils.makeConnection;

public class UserDao {
    public List<User> getAll() {
        List<User> users = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (Statement stmt = con.createStatement()) {
                try (ResultSet res = stmt.executeQuery("select * from APP.APP_USER")) {
                    while (res.next()) users.add(resultSetToUser(res));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User get(int id) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select * from APP.APP_USER where ID=?")) {
                stmt.setInt(1, id);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) return resultSetToUser(res);
                    else return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getByUsername(String username) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select * from APP.APP_USER where USERNAME=?")) {
                stmt.setString(1, username);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) return resultSetToUser(res);
                    else return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existByUsername(String username) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select ID from APP.APP_USER where USERNAME=?")) {
                stmt.setString(1, username);
                try (ResultSet res = stmt.executeQuery()) {
                    return res.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(User user) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "insert into APP.APP_USER (USERNAME, PASSWORD, IS_OWNER, PERMISSION, AVATAR_IMAGE_URL) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setBoolean(3, user.isOwner());
                stmt.setString(4, user.getPermission().name());
                stmt.setString(5, user.getAvatarImageUrl());

                if (stmt.executeUpdate() == 0) throw new InsertFailedException("user");
                try (ResultSet res = stmt.getGeneratedKeys()) {
                    if (res.next()) user.setId(res.getInt(1));
                    else throw new CouldNotRetrieveGeneratedIdException("user");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(User user) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.APP_USER set USERNAME=?, PASSWORD=?, IS_OWNER=?, PERMISSION=?, AVATAR_IMAGE_URL=? where ID=?")) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setBoolean(3, user.isOwner());
                stmt.setString(4, user.getPermission().name());
                stmt.setString(5, user.getAvatarImageUrl());
                stmt.setInt(6, user.getId());
                if (stmt.executeUpdate() == 0)
                    throw new UpdateFailedException("user", user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "delete from APP.APP_USER where ID=?")) {
                stmt.setInt(1, id);
                if (stmt.executeUpdate() == 0)
                    throw new DeleteFailedException("user", id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User resultSetToUser(ResultSet r) throws SQLException {
        User user = new User();
        user.setId(r.getInt("ID"));
        user.setUsername(r.getString("USERNAME"));
        user.setPassword(r.getString("PASSWORD"));
        user.setPermission(Permission.valueOf(r.getString("PERMISSION")));
        user.setOwner(r.getBoolean("IS_OWNER"));
        user.setAvatarImageUrl(r.getString("AVATAR_IMAGE_URL"));
        return user;
    }
}
