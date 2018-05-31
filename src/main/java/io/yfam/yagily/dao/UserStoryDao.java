package io.yfam.yagily.dao;

import io.yfam.yagily.dao.exceptions.CouldNotRetrieveGeneratedIdException;
import io.yfam.yagily.dao.exceptions.InsertFailedException;
import io.yfam.yagily.dao.exceptions.UpdateFailedException;
import io.yfam.yagily.dto.Permission;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.dto.UserStoryState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.yfam.yagily.dao.utils.DaoUtils.*;

public class UserStoryDao {
    public UserStory get(int id) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select U.ID, U.TITLE, U.DESCRIPTION, U.PROJECT_ID, U.ASSIGNEE_USER_ID, U.ESTIMATE_STORY_POINTS, U.STATE, S.USERNAME, S.PERMISSION, S.IS_OWNER, U.BEING_IN_SPRINT from APP.USER_STORY U left join APP.APP_USER S on U.ASSIGNEE_USER_ID = S.ID where U.ID=?")) {
                stmt.setInt(1, id);
                try (ResultSet res = stmt.executeQuery()) {
                    if (!res.next()) return null;
                    UserStory userStory = new UserStory();
                    userStory.setId(res.getInt(1));
                    userStory.setTitle(res.getString(2));
                    userStory.setDescription(res.getString(3));
                    userStory.setProjectId(res.getInt(4));
                    userStory.setAssigneeUserId(getInteger(res, 5));
                    userStory.setEstimateStoryPoints(getInteger(res, 6));
                    userStory.setState(UserStoryState.values()[res.getInt(7)]);
                    tryReadAssigneeUser(res, userStory);
                    userStory.setBeingInSprint(getInteger(res, 11));
                    return userStory;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserStory> getAllByProjectAndStateAndNullSprint(int projectId, int state) {
        List<UserStory> userStoryList = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select U.ID, U.TITLE, U.DESCRIPTION, U.PROJECT_ID, U.ASSIGNEE_USER_ID, U.ESTIMATE_STORY_POINTS, U.STATE, S.USERNAME, S.PERMISSION, S.IS_OWNER, U.BEING_IN_SPRINT from APP.USER_STORY U left join APP.APP_USER S on U.ASSIGNEE_USER_ID = S.ID where U.PROJECT_ID=? and U.STATE=? and U.BEING_IN_SPRINT is null and U.FINISHED_IN_SPRINT is null")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, state);
                tryGetAndAddUserStory(userStoryList, stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userStoryList;
    }

    public int getNumberOfUserStoriesPreparedForSprint(int projectId, int sprint) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select COUNT(U.ID) from APP.USER_STORY U where U.PROJECT_ID=? AND U.BEING_IN_SPRINT=? AND STATE=?")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, sprint);
                stmt.setInt(3, UserStoryState.BACKLOG.ordinal());
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) return (int) res.getLong(1);
                    throw new RuntimeException("Error while counting user story.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<UserStory> getAllByProjectAndBeingSprintAndState(int projectId, int beingInSprint, int state) {
        List<UserStory> userStoryList = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select U.ID, U.TITLE, U.DESCRIPTION, U.PROJECT_ID, U.ASSIGNEE_USER_ID, U.ESTIMATE_STORY_POINTS, U.STATE, S.USERNAME, S.PERMISSION, S.IS_OWNER, U.BEING_IN_SPRINT from APP.USER_STORY U left join APP.APP_USER S on U.ASSIGNEE_USER_ID = S.ID where U.PROJECT_ID=? AND U.BEING_IN_SPRINT=? AND STATE=?")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, beingInSprint);
                stmt.setInt(3, state);
                tryGetAndAddUserStory(userStoryList, stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userStoryList;
    }

    public List<UserStory> getAllByProjectAndBeingSprint(int projectId, int beingInSprint) {
        List<UserStory> userStoryList = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select U.ID, U.TITLE, U.DESCRIPTION, U.PROJECT_ID, U.ASSIGNEE_USER_ID, U.ESTIMATE_STORY_POINTS, U.STATE, S.USERNAME, S.PERMISSION, S.IS_OWNER, U.BEING_IN_SPRINT from APP.USER_STORY U left join APP.APP_USER S on U.ASSIGNEE_USER_ID = S.ID where U.PROJECT_ID=? AND U.BEING_IN_SPRINT=?")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, beingInSprint);
                tryGetAndAddUserStory(userStoryList, stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userStoryList;
    }

    public void insert(UserStory userStory) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "insert into APP.USER_STORY (TITLE, DESCRIPTION, PROJECT_ID, ASSIGNEE_USER_ID, ESTIMATE_STORY_POINTS, BEING_IN_SPRINT) values (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, userStory.getTitle());
                stmt.setString(2, userStory.getDescription());
                stmt.setInt(3, userStory.getProjectId());
                setInteger(stmt, 4, userStory.getAssigneeUserId());
                setInteger(stmt, 5, userStory.getEstimateStoryPoints());
                setInteger(stmt, 6, userStory.getBeingInSprint());

                if (stmt.executeUpdate() == 0) throw new InsertFailedException("user story");
                try (ResultSet res = stmt.getGeneratedKeys()) {
                    if (res.next()) userStory.setId(res.getInt(1));
                    else throw new CouldNotRetrieveGeneratedIdException("user story");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryReadAssigneeUser(ResultSet res, UserStory userStory) throws SQLException {
        if (userStory.getAssigneeUserId() != null) {
            User user = new User();
            user.setId(userStory.getAssigneeUserId());
            user.setUsername(res.getString(8));
            user.setPermission(Permission.valueOf(res.getString(9)));
            user.setOwner(res.getBoolean(10));
            userStory.setAssigneeUser(user);
        }
    }

    private void tryGetAndAddUserStory(List<UserStory> userStoryList, PreparedStatement stmt) throws SQLException {
        try (ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                UserStory userStory = new UserStory();
                userStory.setId(res.getInt(1));
                userStory.setTitle(res.getString(2));
                userStory.setDescription(res.getString(3));
                userStory.setProjectId(res.getInt(4));
                userStory.setAssigneeUserId(getInteger(res, 5));
                userStory.setEstimateStoryPoints(getInteger(res, 6));
                userStory.setState(UserStoryState.values()[res.getInt(7)]);
                tryReadAssigneeUser(res, userStory);
                userStory.setBeingInSprint(getInteger(res, 11));
                userStoryList.add(userStory);
            }
        }
    }

    public void update(UserStory userStory) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.USER_STORY set TITLE=?, DESCRIPTION=?, STATE=?, ASSIGNEE_USER_ID=?, ESTIMATE_STORY_POINTS=?, BEING_IN_SPRINT=? where ID=?")) {
                stmt.setString(1, userStory.getTitle());
                stmt.setString(2, userStory.getDescription());
                stmt.setInt(3, userStory.getState().ordinal());
                setInteger(stmt, 4, userStory.getAssigneeUserId());
                setInteger(stmt, 5, userStory.getEstimateStoryPoints());
                setInteger(stmt, 6, userStory.getBeingInSprint());
                stmt.setInt(7, userStory.getId());
                if (stmt.executeUpdate() == 0)
                    throw new UpdateFailedException("user story", userStory.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetByProjectAndSprintAndNotState(int projectId, int sprint) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.USER_STORY set ASSIGNEE_USER_ID=null, BEING_IN_SPRINT=null, STATE=? where PROJECT_ID=? and BEING_IN_SPRINT=?")) {
                stmt.setInt(1, UserStoryState.BACKLOG.ordinal());
                stmt.setInt(2, projectId);
                stmt.setInt(3, sprint);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFinishedByProjectAndSprintAndState(int projectId, int sprint) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.USER_STORY set FINISHED_IN_SPRINT=? where PROJECT_ID=? and BEING_IN_SPRINT=? and STATE=?")) {
                stmt.setInt(1, sprint);
                stmt.setInt(2, projectId);
                stmt.setInt(3, sprint);
                stmt.setInt(4, UserStoryState.RESOLVED.ordinal());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIfExist(int id) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "delete from APP.USER_STORY where ID=?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
