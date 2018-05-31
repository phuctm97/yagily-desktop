package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.SprintBus;
import io.yfam.yagily.bus.UserStoryBus;
import io.yfam.yagily.dto.Sprint;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.dto.UserStoryState;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.BeautyBoardItem;
import io.yfam.yagily.gui.component.BeautyDummyBoardItem;
import io.yfam.yagily.gui.component.BeautyUpdateBoardItemDialog;
import io.yfam.yagily.gui.context.ActiveSprintContext;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautySprintBoardScreen extends ContextAwareBase implements Initializable {
    private static final String DRAGGING_ITEM = "dragging-item";
    private static final double SCROLL_SPEED = 25.0;

    @FXML
    private StackPane _rootPane;

    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @FXML
    private ScrollPane _scrollPane;

    @FXML
    private HBox _scrollViewPane;

    @FXML
    private Label _listOneTitleText;

    @FXML
    private VBox _listOnePane;

    @FXML
    private VBox _listOnePanePane;

    @FXML
    private ScrollPane _scrollPaneOne;

    @FXML
    private Label _listTwoTitleText;

    @FXML
    private VBox _listTwoPane;

    @FXML
    private VBox _listTwoPanePane;

    @FXML
    private ScrollPane _scrollPaneTwo;

    @FXML
    private Label _listThreeTitleText;

    @FXML
    private VBox _listThreePane;

    @FXML
    private ScrollPane _scrollPaneThree;

    @FXML
    private VBox _listThreePanePane;

    @FXML
    private Label _listFourTitleText;

    @FXML
    private VBox _listFourPane;

    @FXML
    private ScrollPane _scrollPaneFour;

    @FXML
    private VBox _listFourPanePane;

    private Point2D _draggingOffset = new Point2D(0, 0);

    private BeautyBoardItem _draggingItem;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // embedded fonts
        _titleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 16.0));
        _subtitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 11.0));
        _listOneTitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));
        _listTwoTitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));
        _listThreeTitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));
        _listFourTitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 18.0));

        _titleText.setText("Sprint board");
        _subtitleText.textProperty().bind(getContext(ProjectContext.class).projectNameProperty());
        _listOneTitleText.setText("Backlog");
        _listTwoTitleText.setText("New");
        _listThreeTitleText.setText("In Progress");
        _listFourTitleText.setText("Resolved");

        // anchor
        _scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double w = (newValue.doubleValue() - HBox.getMargin(_listTwoPanePane).getLeft() * 3
                    - _scrollViewPane.getPadding().getLeft() - _scrollViewPane.getPadding().getRight()) / 4;
            _listOnePanePane.setPrefWidth(w);
            _listTwoPanePane.setPrefWidth(w);
            _listThreePanePane.setPrefWidth(w);
            _listFourPanePane.setPrefWidth(w);
        });

        Platform.runLater(() -> {
            setupHandleOnScrollPaneScrollBarVisibleChange(_scrollPaneOne, _listOnePane);
            setupHandleOnScrollPaneScrollBarVisibleChange(_scrollPaneTwo, _listTwoPane);
            setupHandleOnScrollPaneScrollBarVisibleChange(_scrollPaneThree, _listThreePane);
            setupHandleOnScrollPaneScrollBarVisibleChange(_scrollPaneFour, _listFourPane);
        });

        // init actions
        loadUserStories();
    }

    private void setupHandleOnScrollPaneScrollBarVisibleChange(ScrollPane scrollPane, VBox view) {
        scrollPane.getChildrenUnmodifiable().stream()
                .filter(it -> (it instanceof ScrollBar) && (((ScrollBar) it).getOrientation() == Orientation.VERTICAL))
                .forEach(it -> {
                    it.visibleProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) view.setPadding(new Insets(0, 0, 0, 0));
                        else view.setPadding(new Insets(0, 10, 0, 0));
                    });

                    if (it.isVisible()) view.setPadding(new Insets(0, 0, 0, 0));
                    else view.setPadding(new Insets(0, 10, 0, 0));
                });
    }

    @FXML
    void onMouseDragEnteredBoardPane(MouseDragEvent event) {

    }

    @FXML
    void onMouseDragExitedBoardPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragOverBoardPane(MouseDragEvent event) {
    }

    @FXML
    void onMouseDragReleasedBoardPane(MouseDragEvent event) {
        if (_draggingItem == null) return;
        if (_draggingItem.getUserStory().getState() != UserStoryState.BACKLOG &&
                event.getSource() == _listOnePanePane) {
            tryMoveUserStory(_draggingItem, UserStoryState.BACKLOG);
        } else if (_draggingItem.getUserStory().getState() != UserStoryState.NEW &&
                event.getSource() == _listTwoPanePane) {
            tryMoveUserStory(_draggingItem, UserStoryState.NEW);
        } else if (_draggingItem.getUserStory().getState() != UserStoryState.IN_PROGRESS &&
                event.getSource() == _listThreePanePane) {
            tryMoveUserStory(_draggingItem, UserStoryState.IN_PROGRESS);
        } else if (_draggingItem.getUserStory().getState() != UserStoryState.RESOLVED &&
                event.getSource() == _listFourPanePane) {
            tryMoveUserStory(_draggingItem, UserStoryState.RESOLVED);
        }
        endDraggingItem();
    }

    @FXML
    void onPressFinishSprint(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Finish sprint");
        alert.setContentText("Do you really want to finish this sprint?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _listOnePane.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            finishSprint();
        }
    }

    void onClickBoardItem(BeautyBoardItem item) {
        ControllerAndNode<BeautyUpdateBoardItemDialog> controllerAndNode =
                loadFXML("/components/beauty-update-board-item-dialog.fxml", BeautyUpdateBoardItemDialog.class, getContextManager());
        controllerAndNode.getController().setUserStory(item.getUserStory());
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }


    private final SprintBus _sprintBus = new SprintBus();

    private final UserStoryBus _userStoryBus = new UserStoryBus();

    private List<BeautyBoardItem> _boardOne = new ArrayList<>();

    private List<BeautyBoardItem> _boardTwo = new ArrayList<>();

    private List<BeautyBoardItem> _boardThree = new ArrayList<>();

    private List<BeautyBoardItem> _boardFour = new ArrayList<>();

    private void loadUserStories() {
        Sprint sprint = getContext(ActiveSprintContext.class).getActiveSprint();

        launch(new Task<List<UserStory>>() {
            @Override
            protected List<UserStory> call() throws Exception {
                return _sprintBus.getAllUserStories(sprint.getProjectId(), sprint.getOrder());
            }

            @Override
            protected void succeeded() {
                clearAllBoards();
                for (UserStory userStory : getValue()) addBoardItem(userStory);
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void clearAllBoards() {
        _listOnePane.getChildren().clear();
        _listTwoPane.getChildren().clear();
        _listThreePane.getChildren().clear();
        _listFourPane.getChildren().clear();
        _boardOne.clear();
        _boardTwo.clear();
        _boardThree.clear();
        _boardFour.clear();
    }

    private void addBoardItem(UserStory userStory) {
        ControllerAndNode<BeautyBoardItem> controllerAndNode
                = loadFXML("/components/beauty-board-item.fxml", BeautyBoardItem.class, getContextManager());
        controllerAndNode.getController().setUserStory(userStory);
        controllerAndNode.getController().setOnDrag(e -> onDragBoardItem(controllerAndNode.getController(), e));
        controllerAndNode.getController().setOnClick(e -> onClickBoardItem(controllerAndNode.getController()));

        VBox.setMargin(controllerAndNode.getNode(), new Insets(6, 0, 6, 0));
        switch (userStory.getState()) {
            case BACKLOG:
                _listOnePane.getChildren().addAll(controllerAndNode.getNode());
                _boardOne.add(controllerAndNode.getController());
                break;
            case NEW:
                _listTwoPane.getChildren().addAll(controllerAndNode.getNode());
                _boardTwo.add(controllerAndNode.getController());
                break;
            case IN_PROGRESS:
                _listThreePane.getChildren().addAll(controllerAndNode.getNode());
                _boardThree.add(controllerAndNode.getController());
                break;
            case RESOLVED:
                _listFourPane.getChildren().addAll(controllerAndNode.getNode());
                _boardFour.add(controllerAndNode.getController());
                break;
        }
    }

    private void onDragBoardItem(BeautyBoardItem item, MouseEvent event) {
        if (event.getEventType() == javafx.scene.input.MouseEvent.DRAG_DETECTED) {
            startDragBoardItem(item, event);
        } else if (event.getEventType() == javafx.scene.input.MouseEvent.MOUSE_DRAGGED) {
            moveDraggingItem(event);
        } else if (event.getEventType() == javafx.scene.input.MouseEvent.MOUSE_RELEASED) {
            endDraggingItem();
        }
    }

    private void startDragBoardItem(BeautyBoardItem item, MouseEvent event) {
        renderDraggingItem((Pane) event.getSource(), item, event);
        _draggingItem = item;
    }

    private void renderDraggingItem(Pane sourceNode, BeautyBoardItem sourceItem, MouseEvent event) {
        sourceItem.setDisabled(true);

        ControllerAndNode<BeautyDummyBoardItem> controllerAndNode =
                loadFXML("/components/beauty-dummy-board-item.fxml", BeautyDummyBoardItem.class);

        BeautyDummyBoardItem copyItem = controllerAndNode.getController();
        copyItem.setUserStory(sourceItem.getUserStory(), getContext(ProjectContext.class).getProjectKey());

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
    }

    private void tryMoveUserStory(BeautyBoardItem item, UserStoryState newState) {
        tryMoveUserStory(item, newState, true);
    }

    private void tryMoveUserStory(BeautyBoardItem item, UserStoryState newState, boolean save) {
        if (item.getUserStory().getState() == newState) return;
        removeBoardItem(item);

        UserStory userStory = item.getUserStory();

        UserStoryState oldState = userStory.getState();
        userStory.setState(newState);

        if (save) {
            launch(new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    _userStoryBus.updateUserStory(userStory);
                    return null;
                }

                @Override
                protected void failed() {
                    tryMoveUserStory(item, oldState, false);
                    showError(getException().getMessage());
                    getException().printStackTrace();
                }
            });
        }

        addBoardItem(userStory);
    }

    private void removeBoardItem(BeautyBoardItem item) {
        switch (item.getUserStory().getState()) {
            case BACKLOG:
                _listOnePane.getChildren().remove(_boardOne.indexOf(item));
                _boardOne.remove(item);
                break;
            case NEW:
                _listTwoPane.getChildren().remove(_boardTwo.indexOf(item));
                _boardTwo.remove(item);
                break;
            case IN_PROGRESS:
                _listThreePane.getChildren().remove(_boardThree.indexOf(item));
                _boardThree.remove(item);
                break;
            case RESOLVED:
                _listFourPane.getChildren().remove(_boardFour.indexOf(item));
                _boardFour.remove(item);
                break;
        }
    }

    private void finishSprint() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _sprintBus.finishSprint(getContext(ActiveSprintContext.class).getActiveSprint());
                return null;
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                navigateToProductBacklogScreen();
            }
        });
    }

    private void navigateToProductBacklogScreen() {
        ControllerAndNode<BeautyProjectScreen> controllerAndNode =
                loadFXML("/screens/beauty-project-screen.fxml", BeautyProjectScreen.class, getContextManager());
        controllerAndNode.getController().setProject(getContext(ProjectContext.class).getProject());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }
}
