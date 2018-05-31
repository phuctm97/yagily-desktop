package io.yfam.yagily.gui.component;

import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.screens.*;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;

public class BeautyMainWindowLeftMenu extends ContextAwareBase implements Initializable {
    @FXML
    private VBox _projectSection;

    @FXML
    private ImageView _projectLogoImage;

    @FXML
    private Label _projectLogoBackupText;

    @FXML
    private Label _projectTitleText;

    @FXML
    private Label _projectSubtitleText;

    @FXML
    private AnchorPane _activeSprintBoardButton;

    @FXML
    private VBox _projectAdministrationSection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _projectSection.managedProperty().bind(_projectSection.visibleProperty());
        _activeSprintBoardButton.managedProperty().bind(_activeSprintBoardButton.visibleProperty());
        _projectAdministrationSection.managedProperty().bind(_projectAdministrationSection.visibleProperty());
        getContextManager().addListener((context, clazz) -> {
            if (ProjectContext.class.isAssignableFrom(clazz)) rebindProjectContext();
        });
        rebindProjectContext();
    }

    @FXML
    void onClickActiveSprintBoard(MouseEvent event) {
        navigateToSprintBoardScreen();
    }

    @FXML
    void onClickProductBacklog(MouseEvent event) {
        navigateToProductBacklogScreen();
    }

    @FXML
    void onClickAllProjects(MouseEvent event) {
        navigateToProjectListScreen();
    }

    @FXML
    void onClickProjectMembers(MouseEvent event) {
        navigateToProjectMembersScreen();
    }

    @FXML
    void onClickProjectSettings(MouseEvent event) {
        navigateToProjectSettingsScreen();
    }

    private void rebindProjectContext() {
        ProjectContext projectContext = getContext(ProjectContext.class);
        if (projectContext == null) {
            _projectSection.setVisible(false);
            return;
        } else {
            _projectSection.setVisible(true);
        }

        _projectTitleText.textProperty().bind(projectContext.projectNameProperty());
        _activeSprintBoardButton.visibleProperty().bind(projectContext.hasActiveSprintProperty());
        _projectSubtitleText.setText(String.format("@%s", projectContext.getProjectKey()));
        projectContext.projectKeyProperty().addListener((observable, oldValue, newValue) -> {
            _projectSubtitleText.setText(String.format("@%s", newValue));
            setProjectLogo(projectContext.getProjectLogoUrl(), projectContext.getProjectKey());
        });
        setProjectLogo(projectContext.getProjectLogoUrl(), projectContext.getProjectKey());
        projectContext.projectLogoUrlProperty().addListener((observable, oldValue, newValue) -> {
            setProjectLogo(newValue, projectContext.getProjectKey());
        });
    }

    private void setProjectLogo(String logo, String key) {
        Image image = null;

        try (InputStream stream = new FileInputStream(logo != null ? logo : "")) {
            image = new Image(stream);
        } catch (IOException ignored) {
            image = null;
        }

        if (image == null || image.isError()) {
            _projectLogoImage.setVisible(false);
            _projectLogoBackupText.setVisible(true);
            _projectLogoBackupText.setText(key.substring(0, key.length() <= 3 ? key.length() : 3));
        } else {
            _projectLogoBackupText.setVisible(false);
            _projectLogoImage.setImage(image);
            _projectLogoImage.setVisible(true);
        }
    }

    private void navigateToProjectSettingsScreen() {
        ControllerAndNode<BeautyProjectSettingScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-setting-screen.fxml", BeautyProjectSettingScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void navigateToProjectMembersScreen() {
        ControllerAndNode<BeautyProjectMemberScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-members-screen.fxml", BeautyProjectMemberScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void navigateToProjectListScreen() {
        ControllerAndNode<BeautyProjectListScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-list-screen.fxml", BeautyProjectListScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void navigateToProductBacklogScreen() {
        ControllerAndNode<BeautyProjectScreen> controllerAndNode =
                loadFXML("/screens/beauty-project-screen.fxml", BeautyProjectScreen.class, getContextManager());
        controllerAndNode.getController().setProject(getContext(ProjectContext.class).getProject());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void navigateToSprintBoardScreen() {
        ControllerAndNode<BeautySprintBoardScreen> controllerAndNode =
                loadFXML("/screens/beauty-sprint-board-screen.fxml", BeautySprintBoardScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

}
