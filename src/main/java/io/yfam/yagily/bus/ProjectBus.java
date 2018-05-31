package io.yfam.yagily.bus;

import io.yfam.yagily.dao.ProjectDao;
import io.yfam.yagily.dao.UserStoryDao;
import io.yfam.yagily.dto.*;
import io.yfam.yagily.gui.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static io.yfam.yagily.dao.utils.DaoConstants.IMAGE_DIRECTORY;

public class ProjectBus {
    private final ProjectDao _projectDao = new ProjectDao();
    private final UserStoryDao _userStoryDao = new UserStoryDao();

    public List<Project> getAllProjectThatUserIsMember(int userId) {
        return _projectDao.getAllThatHasMember(userId);
    }

    public Project createProject(Integer userId, String key, String name, String logo) {
        if (!key.matches("[A-Z0-9]{2,5}")) {
            throw new RuntimeException("Key must contains only uppercase alphabetical letters or digits, and must have at least 2 and at most 5 characters.");
        }

        Project checkKeyProject = _projectDao.getByKey(key);
        if (checkKeyProject != null) {
            throw new RuntimeException(String.format("Key %s has been already used by project %s", key, checkKeyProject.getName()));
        }

        String toSaveLogoFilePath = StringUtils.isNotBlank(logo) ? String.format("%s/%s", IMAGE_DIRECTORY, IOUtils.generateUniqueFileName(logo)) : null;
        if (StringUtils.isNotBlank(toSaveLogoFilePath)) {
            IOUtils.copyFile(logo, toSaveLogoFilePath);
        }

        Project project = new Project();
        project.setKey(key);
        project.setName(name);
        project.setLogoUrl(toSaveLogoFilePath);
        project.setCreateUserId(userId);
        _projectDao.insert(project);

        Member member = new Member();
        member.setUserId(userId);
        member.setRole(Role.OWNER);
        _projectDao.insert(project.getId(), member);

        return project;
    }

    public void updateProject(Project project, String key, String name, String logo,
                              String parts) {
        List<String> partList = Arrays.asList(parts.split(","));

        if (partList.contains("key")) {
            if (!key.matches("[A-Z0-9]{2,5}")) {
                throw new RuntimeException("Key must contains only uppercase alphabetical letters or digits, and must have at least 2 and at most 5 characters.");
            }
            Project checkKeyProject = _projectDao.getByKey(key);
            if (checkKeyProject != null && checkKeyProject.getId() != project.getId()) {
                throw new RuntimeException(String.format("Key %s has been already used by project %s", key, checkKeyProject.getName()));
            }
            project.setKey(key);
        }
        if (partList.contains("name")) {
            project.setName(name);
        }
        if (partList.contains("logo")) {
            String toSaveLogoFilePath = StringUtils.isNotBlank(logo) ? String.format("%s/%s", IMAGE_DIRECTORY, IOUtils.generateUniqueFileName(logo)) : null;
            if (StringUtils.isNotBlank(toSaveLogoFilePath)) {
                IOUtils.copyFile(logo, toSaveLogoFilePath);
            }
            project.setLogoUrl(toSaveLogoFilePath);
        }

        _projectDao.update(project);
    }

    public void deleteProject(Project project) {
        _projectDao.delete(project.getId());
    }

    public void addMember(Project project, int userId, Role role) {
        if (_projectDao.existMember(project.getId(), userId)) {
            throw new RuntimeException("User has already joined the project.");
        }

        Member member = new Member();
        member.setUserId(userId);
        member.setRole(role);
        _projectDao.insert(project.getId(), member);
    }

    public void removeMember(Project project, int userId) {
        _projectDao.deleteMemberIfExist(project.getId(), userId);
    }

    public void updateMemberRole(Project project, int userId, Role role) {
        _projectDao.updateMemberRoleIfExist(project.getId(), userId, role);
    }

    public List<Member> getAllMembers(Project project) {
        return _projectDao.getAllMembersById(project.getId());
    }

    public void getProjectDetails(Project project) {
        project.setMemberList(_projectDao.getAllMembersById(project.getId()));
    }

    public List<UserStory> getProductBacklog(Project project) {
        return _userStoryDao.getAllByProjectAndStateAndNullSprint(project.getId(), UserStoryState.BACKLOG.ordinal());
    }
}
