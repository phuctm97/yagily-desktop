package io.yfam.yagily.gui.component;

import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.screens.BeautyLoginScreen;
import io.yfam.yagily.gui.screens.BeautyUserManagementScreen;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;

public class BeautyMainWindowRightMenu extends ContextAwareBase {
    @FXML
    void onPressLogout(ActionEvent event) {
        closeAndOpenLoginWindow();
    }

    @FXML
    void onClickUserManagement(MouseEvent event) {
        navigateToUserManagement();
    }

    private void navigateToUserManagement() {
        ControllerAndNode<BeautyUserManagementScreen> controllerAndNode
                = loadFXML("/screens/beauty-user-management-screen.fxml", BeautyUserManagementScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }

    private void closeAndOpenLoginWindow() {
        ControllerAndNode<BeautyLoginScreen> controllerAndNode =
                loadFXML("/screens/beauty-login-screen.fxml", BeautyLoginScreen.class, getContextManager());
        getContext(MainWindowContext.class).closeAndOpenNewWindow(controllerAndNode.getNode());
    }
}
