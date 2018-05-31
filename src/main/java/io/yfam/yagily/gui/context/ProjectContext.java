package io.yfam.yagily.gui.context;

import io.yfam.yagily.dto.Project;
import io.yfam.yagily.gui.base.Context;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ProjectContext implements Context {
    private final Project _project;

    private final Property<String> _projectName;

    private final Property<String> _projectKey;

    private final Property<String> _projectLogoUrl;

    private final Property<Boolean> _hasActiveSprint;

    public ProjectContext(Project project) {
        _project = project;
        _projectName = new SimpleStringProperty(_project.getName());
        _projectKey = new SimpleStringProperty(_project.getKey());
        _projectLogoUrl = new SimpleStringProperty(_project.getLogoUrl());
        _hasActiveSprint = new SimpleBooleanProperty(false);
    }

    public Project getProject() {
        return _project;
    }

    public String getProjectName() {
        return _projectName.getValue();
    }

    public Property<String> projectNameProperty() {
        return _projectName;
    }

    public void setProjectName(String projectName) {
        this._projectName.setValue(projectName);
    }

    public String getProjectKey() {
        return _projectKey.getValue();
    }

    public Property<String> projectKeyProperty() {
        return _projectKey;
    }

    public void setProjectKey(String projectKey) {
        this._projectKey.setValue(projectKey);
    }

    public String getProjectLogoUrl() {
        return _projectLogoUrl.getValue();
    }

    public Property<String> projectLogoUrlProperty() {
        return _projectLogoUrl;
    }

    public void setProjectLogoUrl(String projectLogoUrl) {
        this._projectLogoUrl.setValue(projectLogoUrl);
    }

    public Boolean getHasActiveSprint() {
        return _hasActiveSprint.getValue();
    }

    public Property<Boolean> hasActiveSprintProperty() {
        return _hasActiveSprint;
    }

    public void setHasActiveSprint(Boolean hasActiveSprint) {
        this._hasActiveSprint.setValue(hasActiveSprint);
    }
}
