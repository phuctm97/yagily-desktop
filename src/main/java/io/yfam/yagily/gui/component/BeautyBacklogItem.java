package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.ProjectContext;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BeautyBacklogItem extends ContextAwareBase implements Initializable {
    @FXML
    private VBox _typeImagePane;

    @FXML
    private ImageView _typeImage;

    @FXML
    private Label _titleText;

    @FXML
    private Label _codeText;

    @FXML
    private VBox _estimationIcon;

    @FXML
    private Label _estimationText;

    @FXML
    private HBox _rootPane;

    @FXML
    private HBox _moreInfoPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _dragDetected = false;
        _estimationIcon.managedProperty().bind(_estimationIcon.visibleProperty());
    }

    @FXML
    void onClick(MouseEvent event) {
        if (_onClick != null) _onClick.handle(event);
    }

    private void debugDrag(Event event) {
        System.out.println(String.format("Event %s on %s at %d", event.getEventType().getName(), _codeText.getText(), System.currentTimeMillis()));
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


    private UserStory _userStory;

    public UserStory getUserStory() {
        return _userStory;
    }

    public void setUserStory(UserStory userStory, int mode) {
        _userStory = userStory;
        onUpdateUserStory();
    }

    public void setDisabled(boolean disabled) {
        if (disabled) {
            _typeImagePane.setVisible(false);
            _titleText.setVisible(false);
            _moreInfoPane.setVisible(false);
            _rootPane.setDisable(true);
        } else {
            _typeImagePane.setVisible(true);
            _titleText.setVisible(true);
            _moreInfoPane.setVisible(true);
            _rootPane.setDisable(false);
        }
    }

    private EventHandler<MouseEvent> _onClick;

    public EventHandler<MouseEvent> getOnClick() {
        return _onClick;
    }

    public void setOnClick(EventHandler<MouseEvent> onClick) {
        _onClick = onClick;
    }

    private EventHandler<MouseEvent> _onDrag;

    private boolean _dragDetected;

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
