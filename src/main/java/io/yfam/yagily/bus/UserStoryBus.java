package io.yfam.yagily.bus;

import io.yfam.yagily.dao.UserStoryDao;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.dto.UserStoryState;

public class UserStoryBus {
    private final UserStoryDao _userStoryDao = new UserStoryDao();

    public UserStory createUserStory(int projectId, String title, String description) {
        UserStory userStory = new UserStory();
        userStory.setTitle(title);
        userStory.setState(UserStoryState.BACKLOG);
        userStory.setEstimateStoryPoints(null);
        userStory.setAssigneeUserId(null);
        userStory.setDescription(description);
        userStory.setProjectId(projectId);
        _userStoryDao.insert(userStory);

        return userStory;
    }

    public void updateUserStory(UserStory userStory) {
        UserStory oldUserStory = _userStoryDao.get(userStory.getId());
        if (oldUserStory == null)
            throw new RuntimeException(String.format("User story #%d does not exist or has been deleted.", userStory.getId()));
        _userStoryDao.update(userStory);
    }

    public void deleteUserStory(UserStory userStory) {
        UserStory oldUserStory = _userStoryDao.get(userStory.getId());
        if (oldUserStory == null)
            throw new RuntimeException(String.format("User story #%d does not exist or has been deleted.", userStory.getId()));
        _userStoryDao.deleteIfExist(userStory.getId());
    }

    public void moveToSprintBacklog(UserStory userStory, int sprint) {
        userStory.setBeingInSprint(sprint);
        _userStoryDao.update(userStory);
    }

    public void moveToProductBacklog(UserStory userStory) {
        userStory.setBeingInSprint(null);
        _userStoryDao.update(userStory);
    }
}
