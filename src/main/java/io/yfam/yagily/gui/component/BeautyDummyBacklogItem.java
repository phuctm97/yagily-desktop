package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.UserStory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BeautyDummyBacklogItem implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _estimationIcon.managedProperty().bind(_estimationIcon.visibleProperty());
    }

    public void setData(UserStory userStory, String projectKey) {
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
