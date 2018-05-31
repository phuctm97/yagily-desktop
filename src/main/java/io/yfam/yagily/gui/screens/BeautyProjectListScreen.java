package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.ProjectBus;
import io.yfam.yagily.dto.Project;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.BeautyAddCardButton;
import io.yfam.yagily.gui.component.BeautyCardColor;
import io.yfam.yagily.gui.component.BeautyCreateProjectDialog;
import io.yfam.yagily.gui.component.BeautyProjectListItem;
import io.yfam.yagily.gui.context.ActiveSprintContext;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.context.UserContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyProjectListScreen extends ContextAwareBase implements Initializable {
    private final ProjectBus _projectBus = new ProjectBus();

    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @FXML
    private ScrollPane _cardScrollPane;

    @FXML
    private FlowPane _cardPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // embedded fonts
        _titleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 16.0));
        _subtitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 11.0));

        // anchor size card pane to scroll pane
        _cardPane.prefWidthProperty().bind(_cardScrollPane.widthProperty().subtract(16));

        // justify card to width
        final double cardWidth = getProjectCardItemWidth();
        final double cardGap = _cardPane.getHgap();
        final double cardPanePadding = _cardPane.getPadding().getLeft() + _cardPane.getPadding().getRight();
        _cardPane.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            int cardsPerRow = (int) ((newValue.doubleValue() - cardPanePadding + cardGap) / (cardWidth + cardGap));
            int justifyGap = (int) ((newValue.doubleValue() - cardPanePadding - cardsPerRow * cardWidth) / (cardsPerRow - 1));
            _cardPane.setHgap(justifyGap);
        });

        // initial action
        clearProjectContext();

        loadProjects();
    }

    private double getProjectCardItemWidth() {
        ControllerAndNode<BeautyProjectListItem> controllerAndNode
                = loadFXML("/components/beauty-project-list-item.fxml", BeautyProjectListItem.class, getContextManager());
        Pane pane = (Pane) controllerAndNode.getNode();
        return pane.getPrefWidth();
    }

    private void onClickProjectItem(BeautyProjectListItem item) {
        updateProjectContext(item.getProject());
        navigateToProjectScreen(item.getProject());
    }

    private void clearProjectContext() {
        eraseContext(ActiveSprintContext.class);
        eraseContext(ProjectContext.class);
    }

    private void loadProjects() {
        launch(new Task<List<Project>>() {
            @Override
            protected List<Project> call() throws Exception {
                return _projectBus.getAllProjectThatUserIsMember(getContext(UserContext.class).getUser().getId());
            }

            @Override
            protected void succeeded() {
                clearProjectItems();
                for (Project project : getValue()) {
                    addProjectItem(project);
                }
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void clearProjectItems() {
        // add add button to card pane
        ControllerAndNode<BeautyAddCardButton> controllerAndNodeAdd =
                loadFXML("/components/beauty-add-card-button.fxml", BeautyAddCardButton.class, getContextManager());
        controllerAndNodeAdd.getController().setOnClick(e -> openCreateDialog());
        controllerAndNodeAdd.getController().setTitle("Start New Project");
        _cardPane.getChildren().add(controllerAndNodeAdd.getNode());
    }

    private void addProjectItem(Project project) {
        ControllerAndNode<BeautyProjectListItem> controllerAndNode
                = loadFXML("/components/beauty-project-list-item.fxml", BeautyProjectListItem.class, getContextManager());

        controllerAndNode.getController().setProject(project);
        controllerAndNode.getController().setOnClick(e -> onClickProjectItem(controllerAndNode.getController()));
        int cardIndex = (_cardPane.getChildren().size() - 1) % BeautyCardColor.values().length;
        controllerAndNode.getController().setCardColor(BeautyCardColor.values()[cardIndex]);

        _cardPane.getChildren().add(_cardPane.getChildren().size() - 1, controllerAndNode.getNode());
        _subtitleText.setText(String.format("%d projects", _cardPane.getChildren().size() - 1));
    }

    private void updateProjectContext(Project project) {
        putContext(new ProjectContext(project));
    }

    private void navigateToProjectScreen(Project project) {
        ControllerAndNode<BeautyProjectScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-screen.fxml", BeautyProjectScreen.class, getContextManager());
        controllerAndNode.getController().setProject(project);
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void openCreateDialog() {
        ControllerAndNode<BeautyCreateProjectDialog> controllerAndNode =
                loadFXML("/components/beauty-create-project-dialog.fxml", BeautyCreateProjectDialog.class, getContextManager());
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().getCreatedProject() != null) {
                addProjectItem(controllerAndNode.getController().getCreatedProject());
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }
}
