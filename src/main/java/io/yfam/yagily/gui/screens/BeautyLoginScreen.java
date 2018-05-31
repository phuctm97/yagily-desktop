package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.LoginBus;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.UserContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static io.yfam.yagily.gui.base.Store.store;
import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiConstants.APP_NAME;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyLoginScreen extends ContextAwareBase {
    private final LoginBus _loginBus = new LoginBus();

    @FXML
    private TextField _usernameText;

    @FXML
    private PasswordField _passwordText;

    @FXML
    void onPressLogin(ActionEvent event) {
        login();
    }

    private void login() {
        launch(new Task<User>() {
            @Override
            protected User call() throws Exception {
                return _loginBus.login(_usernameText.getText(), _passwordText.getText());
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());// TODO: show error in login screen
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                store().setUser(getValue()); // TODO: remove store
                updateUserContext(getValue());
                closeAndOpenMainWindow();
            }
        });
    }

    private void updateUserContext(User user) {
        UserContext userContext = new UserContext();
        userContext.setUser(user);
        putContext(userContext);
    }

    private void closeAndOpenMainWindow() {
        ControllerAndNode<BeautyMainWindow> controllerAndNode
                = loadFXML("/screens/beauty-main-window.fxml", BeautyMainWindow.class, getContextManager());

        Stage newStage = new Stage();
        newStage.setTitle(APP_NAME);
        newStage.setScene(new Scene(controllerAndNode.getNode()));
        newStage.show();
        newStage.sizeToScene();
        newStage.setMinWidth(newStage.getWidth());
        newStage.setMinHeight(newStage.getHeight());
        ((Stage) _passwordText.getScene().getWindow()).close();
    }
}
