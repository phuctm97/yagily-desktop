package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.ProjectContext;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BeautyBoardItem extends ContextAwareBase implements Initializable {
    @FXML
    private VBox _rootPane;

    @FXML
    private Label _titleText;

    @FXML
    private HBox _epicPane;

    @FXML
    private HBox _epicBack;

    @FXML
    private Label _epicText;

    @FXML
    private VBox _typeImagePane;

    @FXML
    private ImageView _typeImage;

    @FXML
    private Label _estimationText;

    @FXML
    private VBox _estimationIcon;

    @FXML
    private AnchorPane _moreInfoPane;

    @FXML
    private Label _codeText;

    private boolean _dragDetected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _dragDetected = false;
        _epicPane.managedProperty().bind(_epicPane.visibleProperty());
        _epicPane.setVisible(false);
    }

    @FXML
    void onClick(MouseEvent event) {
        if (_onClick != null) _onClick.handle(event);
    }

    @FXML
    void onDragDetected(MouseEvent event) {
        if (event.getSource() != _rootPane) return;
        _rootPane.startFullDrag();
        _dragDetected = true;
        event.consume();
        if (_onDrag != null) _onDrag.handle(event);
    }


    @FXML
    void onMouseDragged(MouseEvent event) {
        if (event.getSource() != _rootPane || !_dragDetected) return;
        event.consume();
        if (_onDrag != null) _onDrag.handle(event);
    }

    @FXML
    void onMouseReleased(MouseEvent event) {
        if (event.getSource() != _rootPane || !_dragDetected) return;
        _dragDetected = false;
        event.consume();
        if (_onDrag != null) _onDrag.handle(event);
    }

    public void setDisabled(boolean disabled) {
        if (disabled) {
            _titleText.setVisible(false);
            _moreInfoPane.setVisible(false);
            _rootPane.setDisable(true);
        } else {
            _titleText.setVisible(true);
            _moreInfoPane.setVisible(true);
            _rootPane.setDisable(false);
        }
    }

    private UserStory _userStory;

    public UserStory getUserStory() {
        return _userStory;
    }

    public void setUserStory(UserStory userStory) {
        _userStory = userStory;
        onUpdateUserStory();
    }

    private EventHandler<MouseEvent> _onClick;

    public EventHandler<MouseEvent> getOnClick() {
        return _onClick;
    }

    public void setOnClick(EventHandler<MouseEvent> onClick) {
        _onClick = onClick;
    }

    private EventHandler<MouseEvent> _onDrag;

    public EventHandler<MouseEvent> getOnDrag() {
        return _onDrag;
    }

    public void setOnDrag(EventHandler<MouseEvent> onDrag) {
        _onDrag = onDrag;
    }

    private void onUpdateUserStory() {
        _codeText.setText(String.format("%s-%d", getContext(ProjectContext.class).getProject().getKey(), _userStory.getId()));
        _titleText.setText(_userStory.getTitle());
        if (_userStory.getEstimateStoryPoints() != null) {
            _estimationIcon.setVisible(true);
            _estimationText.setText(_userStory.getEstimateStoryPoints().toString());
        } else {
            _estimationIcon.setVisible(false);
        }
    }
}
