package io.yfam.yagily.dto;

public class UserStory {
    private int _id;
    private String _title;
    private String _description;
    private UserStoryState _state;
    private Integer _assigneeUserId;
    private Integer _estimateStoryPoints;
    private int _projectId;
    private User _assigneeUser;
    private Integer _beingInSprint;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public UserStoryState getState() {
        return _state;
    }

    public void setState(UserStoryState state) {
        _state = state;
    }

    public Integer getAssigneeUserId() {
        return _assigneeUserId;
    }

    public void setAssigneeUserId(Integer assigneeUserId) {
        _assigneeUserId = assigneeUserId;
    }

    public Integer getEstimateStoryPoints() {
        return _estimateStoryPoints;
    }

    public void setEstimateStoryPoints(Integer estimateStoryPoints) {
        _estimateStoryPoints = estimateStoryPoints;
    }

    public int getProjectId() {
        return _projectId;
    }

    public void setProjectId(int projectId) {
        _projectId = projectId;
    }

    public User getAssigneeUser() {
        return _assigneeUser;
    }

    public void setAssigneeUser(User assigneeUser) {
        _assigneeUser = assigneeUser;
    }

    public Integer getBeingInSprint() {
        return _beingInSprint;
    }

    public void setBeingInSprint(Integer beingInSprint) {
        _beingInSprint = beingInSprint;
    }
}
