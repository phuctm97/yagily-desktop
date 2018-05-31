package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.ProjectBus;
import io.yfam.yagily.bus.SprintBus;
import io.yfam.yagily.bus.UserStoryBus;
import io.yfam.yagily.dto.Project;
import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.*;
import io.yfam.yagily.gui.context.ActiveSprintContext;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyProjectScreen extends ContextAwareBase implements Initializable {
    private final ProjectBus _projectBus = new ProjectBus();

    private final SprintBus _sprintBus = new SprintBus();

    private final UserStoryBus _userStoryBus = new UserStoryBus();

    private static final String DRAGGING_ITEM = "dragging-item";
    private static final double SCROLL_SPEED = 25.0;

    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @FXML
    private Label _sprintText;

    @FXML
    private Button _startSprintButton;

    @FXML
    private Label _productBacklogText;

    @FXML
    private ScrollPane _scrollPane;

    @FXML
    private VBox _scrollViewPane;

    @FXML
    private VBox _nextSprintBacklogPane;

    @FXML
    private HBox _nextSprintDragArea;

    @FXML
    private Label _nextSprintDragAreaText;

    @FXML
    private VBox _productBacklogPane;

    @FXML
    private HBox _addUserStoryButton;

    @FXML
    private StackPane _rootPane;

    private Timeline _scrollTimeline;

    private double _scrollVelocity;

    private int _draggingDirection = 0;

    private Point2D _draggingOffset = new Point2D(0, 0);

    private BeautyBacklogItem _draggingItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // embedded fonts
        _titleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 16.0));
        _subtitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 11.0));
        _sprintText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));
        _productBacklogText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));

        // turn off start sprint button
        _startSprintButton.setVisible(false);

        // manage visibility
        _addUserStoryButton.managedProperty().bind(_addUserStoryButton.visibleProperty());

        // scroll effect
        _scrollVelocity = 0;
        _scrollTimeline = new Timeline();
        _scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        _scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), e -> {
            double v = _scrollPane.getVvalue() + _scrollVelocity;
            _scrollPane.setVvalue(Math.min(1, Math.max(0, v)));
        }));
    }

    @FXML
    void onPressStartSprint(ActionEvent event) {
        openStartSprintDialog();
    }

    @FXML
    void onClickCreateUserStory(MouseEvent event) {
        openCreateUserStoryDialog();
    }

    @FXML
    void onMouseDragEnteredNextSprintPane(MouseDragEvent event) {
        if (_draggingDirection != 0) return;
        _nextSprintDragArea.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), true);
        _nextSprintDragAreaText.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), true);
    }

    @FXML
    void onMouseDragExitedNextSprintPane(MouseDragEvent event) {
        if (_draggingDirection != 0) return;
        _nextSprintDragArea.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), false);
        _nextSprintDragAreaText.pseudoClassStateChanged(PseudoClass.getPseudoClass("highlighted"), false);
    }

    @FXML
    void onMouseDragOverNextSprintPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragReleasedNextSprintPane(MouseDragEvent event) {
        if (_draggingDirection != 0) return;
        moveUserStoryToNextSprint(_draggingItem);
        endDraggingItem();
    }

    @FXML
    void onMouseDragEnteredProductBacklogPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragExitedProductBacklogPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragOverProductBacklogPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragReleasedProductBacklogPane(MouseDragEvent event) {
        if (_draggingDirection != 1) return;
        dropUserStoryToBacklog(_draggingItem);
        endDraggingItem();
    }

    @FXML
    void onMouseDragEnteredScrollPane(MouseDragEvent event) {
        _scrollTimeline.stop();
    }

    @FXML
    void onMouseDragExitedScrollPane(MouseDragEvent event) {
        if (_draggingItem == null) return;

        if (event.getY() < 0) {
            _scrollVelocity = -SCROLL_SPEED / _scrollViewPane.getHeight();
        } else {
            _scrollVelocity = SCROLL_SPEED / _scrollViewPane.getHeight();
        }
        _scrollTimeline.play();
    }

    @FXML
    void onMouseDragReleasedScrollPane(MouseDragEvent event) {
        _scrollTimeline.stop();
    }

    private List<BeautyBacklogItem> _productBacklog = new LinkedList<>();

    private List<BeautyBacklogItem> _nextSprintBacklog = new LinkedList<>();

    private Project _project;

    private Sprint _activeSprint;

    private Sprint _nextSprint;

    public Project getProject() {
        return _project;
    }

    public void setProject(Project project) {
        _project = project;
        onUpdateProject();
    }

    private void setNextSprint(Sprint nextSprint, Sprint activeSprint) {
        _activeSprint = activeSprint;
        if (_activeSprint == null) {
            _startSprintButton.setVisible(true);
        } else {
            _startSprintButton.setVisible(false);
        }
        updateActiveSprintContext();

        _nextSprint = nextSprint;
        _sprintText.setText(String.format("Next Sprint #%d", _nextSprint.getOrder()));
        updateNextSprintBacklog();
    }

    private void updateActiveSprintContext() {
        if (_activeSprint == null) {
            getContext(ProjectContext.class).setHasActiveSprint(false);
            eraseContext(ActiveSprintContext.class);
        } else {
            getContext(ProjectContext.class).setHasActiveSprint(true);
            putContext(new ActiveSprintContext(_activeSprint));
        }
    }

    private void onUpdateProject() {
        _titleText.setText("Product backlog");
        _subtitleText.setText(_project.getName());
        updateProductBacklog();
        updateNextSprint();
    }

    private void updateProductBacklog() {
        launch(new Task<List<UserStory>>() {
            @Override
            protected List<UserStory> call() throws Exception {
                return _projectBus.getProductBacklog(_project);
            }

            @Override
            protected void succeeded() {
                clearProductBacklog();
                for (UserStory userStory : getValue()) addProductBacklogItem(userStory);
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void clearProductBacklog() {
        _productBacklog.clear();
        _productBacklogPane.getChildren().clear();
    }

    private void addProductBacklogItem(UserStory userStory) {
        ControllerAndNode<BeautyBacklogItem> controllerAndNode =
                loadFXML("/components/beauty-backlog-item.fxml", BeautyBacklogItem.class, getContextManager());
        controllerAndNode.getController().setUserStory(userStory, 0);
        controllerAndNode.getController().setOnClick(e -> onClickProductBacklogItem(controllerAndNode.getController()));
        controllerAndNode.getController().setOnDrag(e -> onDragProductBacklogItem(controllerAndNode.getController(), e));

        VBox.setMargin(controllerAndNode.getNode(), new Insets(6, 0, 6, 0));
        _productBacklog.add(controllerAndNode.getController());
        _productBacklogPane.getChildren().add(controllerAndNode.getNode());
    }

    private void onDragProductBacklogItem(BeautyBacklogItem item, MouseEvent event) {
        if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            startDragProductBacklogItem(item, event);
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            moveDraggingItem(event);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            endDraggingItem();
        }
    }

    private void startDragProductBacklogItem(BeautyBacklogItem item, MouseEvent event) {
        renderDraggingItem((Pane) event.getSource(), item, event);
        _draggingDirection = 0;
        _draggingItem = item;
    }

    private void renderDraggingItem(Pane sourceNode, BeautyBacklogItem sourceItem, MouseEvent event) {
        sourceItem.setDisabled(true);

        ControllerAndNode<BeautyDummyBacklogItem> controllerAndNode =
                loadFXML("/components/beauty-dummy-backlog-item.fxml", BeautyDummyBacklogItem.class);

        BeautyDummyBacklogItem copyItem = controllerAndNode.getController();
        copyItem.setData(sourceItem.getUserStory(), getContext(ProjectContext.class).getProjectKey());

        Pane copyNode = (Pane) controllerAndNode.getNode();
        copyNode.setPrefWidth(sourceNode.getWidth());
        copyNode.setPrefHeight(sourceNode.getHeight());

        Pane draggingNode = new Pane(copyNode);
        draggingNode.setId(DRAGGING_ITEM);
        draggingNode.setMouseTransparent(true);
        draggingNode.setManaged(false);
        _rootPane.getChildren().add(draggingNode);

        Point2D loc = _rootPane.sceneToLocal(event.getSceneX() - event.getX(), event.getSceneY() - event.getY());
        _draggingOffset = new Point2D(loc.getX() - event.getSceneX(), loc.getY() - event.getSceneY());
        draggingNode.relocate(event.getSceneX() + _draggingOffset.getX(), event.getSceneY() + _draggingOffset.getY());
    }

    private void moveDraggingItem(MouseEvent event) {
        Node node = _rootPane.lookup(String.format("#%s", DRAGGING_ITEM));
        if (node == null) return;

        node.relocate(event.getSceneX() + _draggingOffset.getX(), event.getSceneY() + _draggingOffset.getY());
    }

    private void endDraggingItem() {
        Node node = _rootPane.lookup(String.format("#%s", DRAGGING_ITEM));
        if (node != null) _rootPane.getChildren().remove(node);

        if (_draggingItem != null) {
            _draggingItem.setDisabled(false);
            _draggingItem = null;
        }
        _scrollTimeline.stop();
    }

    private void onClickProductBacklogItem(BeautyBacklogItem item) {
        openUserStoryDialog(item);
    }

    private void moveUserStoryToNextSprint(BeautyBacklogItem item) {
        int removeIndex = _productBacklog.indexOf(item);
        _productBacklog.remove(removeIndex);
        _productBacklogPane.getChildren().remove(removeIndex);

        addNextSprintBacklogItem(item.getUserStory());

        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _userStoryBus.moveToSprintBacklog(item.getUserStory(), _nextSprint.getOrder());
                return null;
            }

            @Override
            protected void failed() {
                int removeIndex = _nextSprintBacklog.indexOf(item);
                _nextSprintBacklog.remove(removeIndex);
                _nextSprintBacklogPane.getChildren().remove(removeIndex);

                addProductBacklogItem(item.getUserStory());

                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void updateNextSprint() {
        launch(new Task<Pair<Sprint, Sprint>>() {
            @Override
            protected Pair<Sprint, Sprint> call() throws Exception {
                return new Pair<>(_sprintBus.getOrCreateQueuingSprint(_project.getId()),
                        _sprintBus.getActiveSprint(_project.getId()));
            }

            @Override
            protected void succeeded() {
                setNextSprint(getValue().getKey(), getValue().getValue());
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void updateNextSprintBacklog() {
        launch(new Task<List<UserStory>>() {
            @Override
            protected List<UserStory> call() throws Exception {
                return _sprintBus.getSprintBacklog(_project.getId(), _nextSprint.getOrder());
            }

            @Override
            protected void succeeded() {
                clearNextSprintBacklog();
                for (UserStory userStory : getValue()) addNextSprintBacklogItem(userStory);
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
            }
        });
    }

    private void clearNextSprintBacklog() {
        _nextSprintBacklog.clear();
        _nextSprintBacklogPane.getChildren().clear();
    }

    private void addNextSprintBacklogItem(UserStory userStory) {
        ControllerAndNode<BeautyBacklogItem> controllerAndNode =
                loadFXML("/components/beauty-backlog-item.fxml", BeautyBacklogItem.class, getContextManager());
        controllerAndNode.getController().setUserStory(userStory, 1);
        controllerAndNode.getController().setOnDrag(e -> onDragNextSprintBacklogItem(controllerAndNode.getController(), e));

        VBox.setMargin(controllerAndNode.getNode(), new Insets(6, 0, 6, 0));
        _nextSprintBacklog.add(controllerAndNode.getController());
        _nextSprintBacklogPane.getChildren().add(controllerAndNode.getNode());
    }

    private void onDragNextSprintBacklogItem(BeautyBacklogItem item, MouseEvent event) {
        if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            startDragNextSprintBacklogItem(item, event);
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            moveDraggingItem(event);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            endDraggingItem();
        }
    }

    private void startDragNextSprintBacklogItem(BeautyBacklogItem item, MouseEvent event) {
        renderDraggingItem((Pane) event.getSource(), item, event);
        _draggingDirection = 1;
        _draggingItem = item;
    }

    private void dropUserStoryToBacklog(BeautyBacklogItem item) {
        int removeIndex = _nextSprintBacklog.indexOf(item);
        _nextSprintBacklog.remove(removeIndex);
        _nextSprintBacklogPane.getChildren().remove(removeIndex);

        addProductBacklogItem(item.getUserStory());

        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _userStoryBus.moveToProductBacklog(item.getUserStory());
                return null;
            }

            @Override
            protected void failed() {
                int removeIndex = _productBacklog.indexOf(item);
                _productBacklog.remove(removeIndex);
                _productBacklogPane.getChildren().remove(removeIndex);

                addNextSprintBacklogItem(item.getUserStory());

                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void openUserStoryDialog(BeautyBacklogItem item) {
        ControllerAndNode<BeautyBacklogDialog> controllerAndNode =
                loadFXML("/components/beauty-backlog-dialog.fxml", BeautyBacklogDialog.class, getContextManager());
        controllerAndNode.getController().setUserStory(item.getUserStory());
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().getResult() == UserStorySettingsResult.UPDATED) {
                item.setUserStory(controllerAndNode.getController().getUserStory(), 0);
            } else if (controllerAndNode.getController().getResult() == UserStorySettingsResult.DELETED) {
                int removeIndex = _productBacklog.indexOf(item);
                _productBacklogPane.getChildren().remove(removeIndex);
                _productBacklog.remove(removeIndex);
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }

    private void openStartSprintDialog() {
        ControllerAndNode<BeautyStartSprintDialog> controllerAndNode =
                loadFXML("/components/beauty-start-sprint-dialog.fxml", BeautyStartSprintDialog.class, getContextManager());
        controllerAndNode.getController().setSprint(_nextSprint);
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().isStarted()) {
                _activeSprint = controllerAndNode.getController().getSprint();
                _nextSprint = null;
                updateActiveSprintContext();
                navigateToActiveSprintScreen();
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }

    private void openCreateUserStoryDialog() {
        ControllerAndNode<BeautyCreateUserStoryDialog> controllerAndNode =
                loadFXML("/components/beauty-create-user-story-dialog.fxml", BeautyCreateUserStoryDialog.class, getContextManager());
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().getCreatedUserStory() != null) {
                addProductBacklogItem(controllerAndNode.getController().getCreatedUserStory());
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }

    private void navigateToActiveSprintScreen() {
        ControllerAndNode<BeautySprintBoardScreen> controllerAndNode =
                loadFXML("/screens/beauty-sprint-board-screen.fxml", BeautySprintBoardScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }
}
