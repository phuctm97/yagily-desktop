package io.yfam.yagily.bus;

import io.yfam.yagily.dao.exceptions.InsertFailedException;
import io.yfam.yagily.dao.exceptions.UpdateFailedException;
import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.dto.SprintState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.yfam.yagily.dao.utils.DaoUtils.makeConnection;

public class SprintDao {
    public int getTotalSprints(int projectId) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select COUNT(DISTINCT ORDER_NUMBER) from APP.SPRINT where PROJECT_ID=?")) {
                stmt.setInt(1, projectId);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) return (int) res.getLong(1);
                    throw new RuntimeException("Error while count number of sprint.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Sprint get(int projectId, int order) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select ORDER_NUMBER, PROJECT_ID, START_DATE, END_DATE, STATE, DESCRIPTION from APP.SPRINT where PROJECT_ID=? AND ORDER_NUMBER=?")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, order);
                return tryParseResultSetToSprint(stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Sprint get(int projectId, SprintState state) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select ORDER_NUMBER, PROJECT_ID, START_DATE, END_DATE, STATE, DESCRIPTION from APP.SPRINT where PROJECT_ID=? AND STATE=?")) {
                stmt.setInt(1, projectId);
                stmt.setString(2, state.name());
                return tryParseResultSetToSprint(stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Sprint tryParseResultSetToSprint(PreparedStatement stmt) throws SQLException {
        try (ResultSet res = stmt.executeQuery()) {
            if (!res.next()) return null;
            Sprint sprint = new Sprint();
            sprint.setOrder(res.getInt(1));
            sprint.setProjectId(res.getInt(2));
            sprint.setStartDate(res.getDate(3));
            sprint.setEndDate(res.getDate(4));
            sprint.setState(SprintState.valueOf(res.getString(5)));
            sprint.setDescription(res.getString(6));
            return sprint;
        }
    }

    public void insert(Sprint sprint) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "insert into APP.SPRINT (ORDER_NUMBER, PROJECT_ID, START_DATE, END_DATE, DESCRIPTION, STATE) values (?,?,?,?,?,?)")) {
                stmt.setInt(1, sprint.getOrder());
                stmt.setInt(2, sprint.getProjectId());
                stmt.setDate(3, sprint.getStartDate());
                stmt.setDate(4, sprint.getEndDate());
                stmt.setString(5, sprint.getDescription());
                stmt.setString(6, sprint.getState().name());
                if (stmt.executeUpdate() == 0) throw new InsertFailedException("sprint");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Sprint sprint) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.SPRINT set START_DATE=?, END_DATE=?, DESCRIPTION=?, STATE=? where PROJECT_ID=? AND ORDER_NUMBER=?")) {
                stmt.setDate(1, sprint.getStartDate());
                stmt.setDate(2, sprint.getEndDate());
                stmt.setString(3, sprint.getDescription());
                stmt.setString(4, sprint.getState().name());
                stmt.setInt(5, sprint.getProjectId());
                stmt.setInt(6, sprint.getOrder());
                if (stmt.executeUpdate() == 0)
                    throw new UpdateFailedException("sprint", sprint.getProjectId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
