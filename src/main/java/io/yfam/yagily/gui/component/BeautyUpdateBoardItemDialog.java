package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.UserStoryBus;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class BeautyUpdateBoardItemDialog extends ContextAwareBase implements Initializable {
    @FXML
    private Label _titleLabel;

    @FXML
    private Label _codeText;

    @FXML
    private TextField _titleText;

    @FXML
    private TextArea _descriptionText;

    @FXML
    private TextField _estimationText;

    @FXML
    private ImageView _assigneeUsernameImage;

    @FXML
    private Label _assigneeUsernameText;

    @FXML
    private Button _updateButton;

    @FXML
    private Button _deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 28.0));
        _assigneeUsernameImage.setClip(new Circle(_assigneeUsernameImage.getFitWidth() * 0.5, _assigneeUsernameImage.getFitHeight() * 0.5, _assigneeUsernameImage.getFitWidth() * 0.5));
    }

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressDelete(ActionEvent event) {

    }

    @FXML
    void onPressUpdate(ActionEvent event) {

    }

    private final UserStoryBus _userStoryBus = new UserStoryBus();

    private UserStory _userStory;

    public UserStory getUserStory() {
        return _userStory;
    }

    public void setUserStory(UserStory userStory) {
        _userStory = userStory;
        onUpdateUserStory();
    }

    private void onUpdateUserStory() {
        _codeText.setText(String.format("%s-%d", getContext(ProjectContext.class).getProjectKey(), _userStory.getId()));
        _titleText.setText(_userStory.getTitle());
        _descriptionText.setText(_userStory.getDescription());
        if (_userStory.getEstimateStoryPoints() != null) {
            _estimationText.setText(Integer.toString(_userStory.getEstimateStoryPoints()));
        }
        if (_userStory.getAssigneeUser() != null) {
            _assigneeUsernameText.setText(_userStory.getAssigneeUser().getUsername());
            setAssigneeImage(_userStory.getAssigneeUser().getAvatarImageUrl());
        } else {
            _assigneeUsernameText.setText("[not assigned]");
            _assigneeUsernameImage.setImage(null);
        }
    }

    private void setAssigneeImage(String url) {
        if (StringUtils.isBlank(url)) return;

        Image image = null;

        try (InputStream stream = new FileInputStream(url)) {
            image = new Image(stream);
        } catch (IOException ignored) {
            image = null;
        }

        if (image != null && !image.isError()) {
            _assigneeUsernameImage.setImage(image);
        }
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
    }
}
