package io.yfam.yagily.bus;

import io.yfam.yagily.dao.UserStoryDao;
import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.dto.SprintState;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.dto.UserStoryState;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SprintBus {
    private final SprintDao _sprintDao = new SprintDao();
    private final UserStoryDao _userStoryDao = new UserStoryDao();

    public Sprint getActiveSprint(int projectId) {
        return _sprintDao.get(projectId, SprintState.ACTIVE);
    }

    public Sprint getOrCreateQueuingSprint(int projectId) {
        Sprint sprint = _sprintDao.get(projectId, SprintState.QUEUING);
        if (sprint != null) return sprint;

        sprint = new Sprint();
        sprint.setProjectId(projectId);
        sprint.setOrder(_sprintDao.getTotalSprints(projectId) + 1);
        sprint.setStartDate(null);
        sprint.setEndDate(null);
        sprint.setDescription(null);
        sprint.setState(SprintState.QUEUING);
        _sprintDao.insert(sprint);
        return sprint;
    }

    public List<UserStory> getSprintBacklog(int projectId, int sprint) {
        return _userStoryDao.getAllByProjectAndBeingSprintAndState(projectId, sprint, UserStoryState.BACKLOG.ordinal());
    }

    public List<UserStory> getAllUserStories(int projectId, int sprint) {
        return _userStoryDao.getAllByProjectAndBeingSprint(projectId, sprint);
    }

    public void startSprint(Sprint sprint, LocalDate endDate, String description) {
        if (getActiveSprint(sprint.getProjectId()) != null) {
            throw new RuntimeException("There still one sprint running. Only one sprint can run at a time.");
        }
        if (_userStoryDao.getNumberOfUserStoriesPreparedForSprint(sprint.getProjectId(), sprint.getOrder()) == 0) {
            throw new RuntimeException("Sprint must have at least one user story in backlog to start.");
        }

        sprint.setStartDate(Date.valueOf(LocalDate.now()));
        sprint.setEndDate(Date.valueOf(endDate));
        sprint.setDescription(description);
        sprint.setState(SprintState.ACTIVE);
        _sprintDao.update(sprint);
    }

    public void finishSprint(Sprint sprint) {
        _userStoryDao.setFinishedByProjectAndSprintAndState(sprint.getProjectId(), sprint.getOrder());
        _userStoryDao.resetByProjectAndSprintAndNotState(sprint.getProjectId(), sprint.getOrder());
        sprint.setState(SprintState.FINISHED);
        _sprintDao.update(sprint);
    }
}
