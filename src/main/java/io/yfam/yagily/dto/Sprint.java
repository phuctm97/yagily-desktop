package io.yfam.yagily.dto;

import java.sql.Date;

public class Sprint {
    private int _projectId;
    private int _order;
    private Date _startDate;
    private Date _endDate;
    private String _description;
    private SprintState _state;

    public int getProjectId() {
        return _projectId;
    }

    public void setProjectId(int projectId) {
        _projectId = projectId;
    }

    public int getOrder() {
        return _order;
    }

    public void setOrder(int order) {
        _order = order;
    }

    public Date getStartDate() {
        return _startDate;
    }

    public void setStartDate(Date startDate) {
        _startDate = startDate;
    }

    public Date getEndDate() {
        return _endDate;
    }

    public void setEndDate(Date endDate) {
        _endDate = endDate;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public SprintState getState() {
        return _state;
    }

    public void setState(SprintState state) {
        _state = state;
    }
}
