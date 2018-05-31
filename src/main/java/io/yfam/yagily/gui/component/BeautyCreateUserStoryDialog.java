package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.UserStoryBus;
import io.yfam.yagily.dto.UserStory;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.base.FutureDone;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyCreateUserStoryDialog extends ContextAwareBase implements FutureDone {
    @FXML
    private TextField _titleText;

    @FXML
    private TextArea _descriptionText;

    @FXML
    private Button _createButton;

    private Runnable _doneHandler;

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressCreate(ActionEvent event) {
        createUserStory();
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }

    private final UserStoryBus _userStoryBus = new UserStoryBus();

    private UserStory _createdUserStory;

    public UserStory getCreatedUserStory() {
        return _createdUserStory;
    }

    private void createUserStory() {
        launch(new Task<UserStory>() {
            @Override
            protected UserStory call() throws Exception {
                return _userStoryBus.createUserStory(getContext(ProjectContext.class).getProject().getId(),
                        _titleText.getText(), _descriptionText.getText());
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                _createdUserStory = getValue();
                closeSelf();
            }
        });
    }

    @Override
    public void onDone(Runnable handler) {
        _doneHandler = handler;
    }
}
