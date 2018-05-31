package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.UserStoryBus;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.base.FutureDone;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.screens.UserStorySettingsResult;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyBacklogDialog extends ContextAwareBase implements Initializable, FutureDone {
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
    private Button _updateButton;

    @FXML
    private Button _deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 28.0));
    }

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressUpdate(ActionEvent event) {
        updateUserStory();
    }

    @FXML
    void onPressDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete user story");
        alert.setContentText("Do you really want to delete?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _titleText.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteUserStory();
        }
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
    }

    private UserStorySettingsResult _result = UserStorySettingsResult.NONE;

    public UserStorySettingsResult getResult() {
        return _result;
    }

    private void updateUserStory() {
        _userStory.setTitle(_titleText.getText());
        _userStory.setDescription(_descriptionText.getText());
        if (StringUtils.isBlank(_estimationText.getText())) {
            _userStory.setEstimateStoryPoints(null);
        } else {
            _userStory.setEstimateStoryPoints(Integer.parseInt(_estimationText.getText()));
        }

        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _userStoryBus.updateUserStory(_userStory);
                return null;
            }

            @Override
            protected void succeeded() {
                _result = UserStorySettingsResult.UPDATED;
                closeSelf();
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void deleteUserStory() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _userStoryBus.deleteUserStory(_userStory);
                return null;
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                _result = UserStorySettingsResult.DELETED;
                closeSelf();
            }
        });
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }

    private Runnable _doneHandler;

    @Override
    public void onDone(Runnable handler) {
        _doneHandler = handler;
    }
}
