package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.UserStory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BeautyDummyBoardItem implements Initializable {
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
    private Label _codeText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _epicPane.managedProperty().bind(_epicPane.visibleProperty());
        _epicPane.setVisible(false);
    }

    public void setUserStory(UserStory userStory, String projectKey) {
        _codeText.setText(String.format("%s-%d", projectKey, userStory.getId()));
        _titleText.setText(userStory.getTitle());
        if (userStory.getEstimateStoryPoints() != null) {
            _estimationIcon.setVisible(true);
            _estimationText.setText(userStory.getEstimateStoryPoints().toString());
        } else {
            _estimationIcon.setVisible(false);
        }
    }
}
