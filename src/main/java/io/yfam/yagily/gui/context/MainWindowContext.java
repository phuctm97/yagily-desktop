package io.yfam.yagily.gui.context;

import io.yfam.yagily.gui.base.Context;
import io.yfam.yagily.gui.base.RunnableP;
import javafx.scene.Parent;

public class MainWindowContext implements Context {
    private final RunnableP<Parent> _navigateHandler;
    private final Runnable _closeWindowHandler;
    private final RunnableP<Parent> _closeAndOpenNewWindowHandler;
    private final RunnableP<Boolean> _setLoadingOverlayVisibleHandler;
    private final RunnableP<Parent> _showDialogHandler;
    private final Runnable _closeDialogHandler;

    public MainWindowContext(RunnableP<Parent> navigateHandler, Runnable closeWindowHandler, RunnableP<Parent> closeAndOpenNewWindowHandler, RunnableP<Boolean> setLoadingOverlayVisibleHandler, RunnableP<Parent> showDialogHandler, Runnable closeDialogHandler) {
        _navigateHandler = navigateHandler;
        _closeWindowHandler = closeWindowHandler;
        _closeAndOpenNewWindowHandler = closeAndOpenNewWindowHandler;
        _setLoadingOverlayVisibleHandler = setLoadingOverlayVisibleHandler;
        _showDialogHandler = showDialogHandler;
        _closeDialogHandler = closeDialogHandler;
    }

    public void navigate(Parent node) {
        _navigateHandler.run(node);
    }

    public void closeWindow() {
        _closeWindowHandler.run();
    }

    public void closeAndOpenNewWindow(Parent root) {
        _closeAndOpenNewWindowHandler.run(root);
    }

    public void showLoadingOverlay() {
        _setLoadingOverlayVisibleHandler.run(true);
    }

    public void hideLoadingOverlay() {
        _setLoadingOverlayVisibleHandler.run(false);
    }

    public void showDialog(Parent root) {
        _showDialogHandler.run(root);
    }

    public void closeDialog() {
        _closeDialogHandler.run();
    }
}
