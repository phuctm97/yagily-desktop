package io.yfam.yagily.dao.utils;

import java.sql.*;

public final class DaoUtils {
    private static final String DB_PATH = ".data";
    private static final String DB_USER = "yagily";
    private static final String DB_PASSWORD = "derby";

    public static Connection makeConnection() {
        try {
            return DriverManager.getConnection(
                    String.format("jdbc:derby:%s;user=%s;password=%s;create=true", DB_PATH, DB_USER, DB_PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getInteger(ResultSet res, int column) throws SQLException {
        int r = res.getInt(column);
        if (res.wasNull()) return null;
        return r;
    }

    public static void setInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
