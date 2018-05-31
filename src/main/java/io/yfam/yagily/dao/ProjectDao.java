package io.yfam.yagily.dao;

import io.yfam.yagily.dao.exceptions.CouldNotRetrieveGeneratedIdException;
import io.yfam.yagily.dao.exceptions.DeleteFailedException;
import io.yfam.yagily.dao.exceptions.InsertFailedException;
import io.yfam.yagily.dao.exceptions.UpdateFailedException;
import io.yfam.yagily.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.yfam.yagily.dao.utils.DaoUtils.*;

public class ProjectDao {
    public List<Project> getAllThatHasMember(int memberUserId) {
        List<Project> projectList = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select P.ID, P.KEY_NAME, P.FULL_NAME, P.LOGO, P.CREATE_USER_ID from APP.PROJECT_MEMBER M JOIN APP.PROJECT P ON M.PROJECT_ID = P.ID WHERE M.USER_ID=?")) {
                stmt.setInt(1, memberUserId);
                try (ResultSet res = stmt.executeQuery()) {
                    while (res.next()) {
                        Project project = new Project();
                        project.setId(res.getInt(1));
                        project.setKey(res.getString(2));
                        project.setName(res.getString(3));
                        project.setLogoUrl(res.getString(4));
                        project.setCreateUserId(getInteger(res, 5));
                        projectList.add(project);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return projectList;
    }

    public List<Member> getAllMembersById(int projectId) {
        List<Member> memberList = new ArrayList<>();

        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select P.USER_ID, P.MEMBER_ROLE, U.USERNAME, U.PERMISSION, U.IS_OWNER, U.AVATAR_IMAGE_URL from APP.PROJECT_MEMBER P JOIN APP.APP_USER U ON P.USER_ID = U.ID WHERE P.PROJECT_ID=?")) {
                stmt.setInt(1, projectId);
                try (ResultSet res = stmt.executeQuery()) {
                    while (res.next()) {
                        Member member = new Member();
                        User user = new User();
                        user.setId(res.getInt(1));
                        user.setUsername(res.getString(3));
                        user.setPermission(Permission.valueOf(res.getString(4)));
                        user.setOwner(res.getBoolean(5));
                        user.setAvatarImageUrl(res.getString(6));
                        member.setUser(user);
                        member.setRole(Role.valueOf(res.getString(2)));
                        memberList.add(member);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return memberList;
    }

    public boolean existMember(int projectId, int userId) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select P.USER_ID FROM APP.PROJECT_MEMBER P WHERE P.USER_ID=? AND P.PROJECT_ID=?")) {
                stmt.setInt(1, userId);
                stmt.setInt(2, projectId);
                try (ResultSet res = stmt.executeQuery()) {
                    return res.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Project getByKey(String key) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select P.ID, P.KEY_NAME, P.FULL_NAME, P.LOGO, P.CREATE_USER_ID from APP.PROJECT P WHERE P.KEY_NAME=?")) {
                stmt.setString(1, key);
                try (ResultSet res = stmt.executeQuery()) {
                    if (!res.next()) return null;

                    Project project = new Project();
                    project.setId(res.getInt(1));
                    project.setKey(res.getString(2));
                    project.setName(res.getString(3));
                    project.setLogoUrl(res.getString(4));
                    project.setCreateUserId(getInteger(res, 5));
                    return project;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsByKey(String key) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "select P.ID from APP.PROJECT P WHERE P.KEY_NAME=?")) {
                stmt.setString(1, key);
                try (ResultSet res = stmt.executeQuery()) {
                    return res.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Project project) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "insert into APP.PROJECT (KEY_NAME, FULL_NAME, LOGO, CREATE_USER_ID) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, project.getKey());
                stmt.setString(2, project.getName());
                stmt.setString(3, project.getLogoUrl());
                setInteger(stmt, 4, project.getCreateUserId());

                if (stmt.executeUpdate() == 0) throw new InsertFailedException("project");
                try (ResultSet res = stmt.getGeneratedKeys()) {
                    if (res.next()) project.setId(res.getInt(1));
                    else throw new CouldNotRetrieveGeneratedIdException("project");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(int projectId, Member member) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "insert into APP.PROJECT_MEMBER (PROJECT_ID, USER_ID, MEMBER_ROLE) values (?,?,?)")) {
                stmt.setInt(1, projectId);
                stmt.setInt(2, member.getUserId());
                stmt.setString(3, member.getRole().name());
                if (stmt.executeUpdate() == 0) throw new InsertFailedException("project");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Project project) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.PROJECT set KEY_NAME=?, FULL_NAME=?, LOGO=?, CREATE_USER_ID=? where ID=?")) {
                stmt.setString(1, project.getKey());
                stmt.setString(2, project.getName());
                stmt.setString(3, project.getLogoUrl());
                setInteger(stmt, 4, project.getCreateUserId());
                stmt.setInt(5, project.getId());
                if (stmt.executeUpdate() == 0)
                    throw new UpdateFailedException("project", project.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "delete from APP.PROJECT where ID=?")) {
                stmt.setInt(1, id);
                if (stmt.executeUpdate() == 0)
                    throw new DeleteFailedException("project", id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMemberIfExist(int projectId, int userId) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "delete from APP.PROJECT_MEMBER where USER_ID=? AND PROJECT_ID=?")) {
                stmt.setInt(1, userId);
                stmt.setInt(2, projectId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMemberRoleIfExist(int projectId, int userId, Role role) {
        try (Connection con = makeConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(
                    "update APP.PROJECT_MEMBER set MEMBER_ROLE=? where PROJECT_ID=? AND USER_ID=?")) {
                stmt.setString(1, role.name());
                stmt.setInt(2, projectId);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
