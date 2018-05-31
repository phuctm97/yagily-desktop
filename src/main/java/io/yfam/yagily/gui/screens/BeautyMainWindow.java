package io.yfam.yagily.gui.screens;

import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.BeautyMainWindowLeftMenu;
import io.yfam.yagily.gui.component.BeautyMainWindowRightMenu;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.GuiConstants.APP_NAME;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;

public class BeautyMainWindow extends ContextAwareBase implements Initializable {
    @FXML
    private StackPane _rootStackPane;

    @FXML
    private StackPane _mainPane;

    @FXML
    private AnchorPane _headerPane;

    @FXML
    private AnchorPane _menuPane;

    @FXML
    private VBox _leftMenuPane;

    @FXML
    private VBox _rightMenuPane;

    @FXML
    private AnchorPane _loadingPane;

    @FXML
    private StackPane _dialogPane;

    private boolean _changingMainScreen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _changingMainScreen = false;
        updateMainWindowContext();

        // ui setup
        _menuPane.managedProperty().bind(_mainPane.visibleProperty());
        _menuPane.setVisible(false);
        _leftMenuPane.setVisible(false);
        _rightMenuPane.setVisible(false);
        putMenus();

        _loadingPane.managedProperty().bind(_loadingPane.visibleProperty());
        _loadingPane.setVisible(false);

        _dialogPane.managedProperty().bind(_dialogPane.visibleProperty());
        _dialogPane.setVisible(false);

        // initial actions
        navigateToProjectListScreen();
    }

    private void putMenus() {
        // left menu
        ControllerAndNode<BeautyMainWindowLeftMenu> controllerAndNodeLeft
                = loadFXML("/components/beauty-main-window-left-menu.fxml", BeautyMainWindowLeftMenu.class, getContextManager());
        _leftMenuPane.getChildren().add(controllerAndNodeLeft.getNode());

        // right menu
        ControllerAndNode<BeautyMainWindowRightMenu> controllerAndNodeRight
                = loadFXML("/components/beauty-main-window-right-menu.fxml", BeautyMainWindowRightMenu.class, getContextManager());
        _rightMenuPane.getChildren().add(controllerAndNodeRight.getNode());
    }

    @FXML
    void onPressProjectsMenu(ActionEvent event) {
        openLeftMenu();
    }

    @FXML
    void onClickRightMenu(MouseEvent event) {
        openRightMenu();
    }

    @FXML
    void onClickMenuPane(MouseEvent event) {
        closeMenu();
    }

    @FXML
    void onClickDialogPane(MouseEvent event) {
    }

    private void updateMainWindowContext() {
        putContext(new MainWindowContext(this::changeMainScreen,
                this::closeSelf, this::closeSelfAndOpenNew,
                this::setLoadingPaneVisible, this::showDialog, this::closeDialog));
    }

    private void navigateToProjectListScreen() {
        ControllerAndNode<BeautyProjectListScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-list-screen.fxml", BeautyProjectListScreen.class, getContextManager());
        changeMainScreen(controllerAndNode.getNode());
    }

    private void changeMainScreen(Parent node) {
        if (_changingMainScreen) return;

        if (!_mainPane.getChildren().isEmpty()) {
            _changingMainScreen = true;

            _mainPane.getChildren().add(node);
            node.setTranslateX(_mainPane.getWidth());

            Node exitNode = _mainPane.getChildren().get(0);
            TranslateTransition exitTransition = new TranslateTransition(Duration.millis(200), exitNode);
            exitTransition.setByX(-_mainPane.getWidth());

            TranslateTransition enterTransition = new TranslateTransition(Duration.millis(200), node);
            enterTransition.setByX(-_mainPane.getWidth());

            ParallelTransition transition = new ParallelTransition(exitTransition, enterTransition);
            transition.setOnFinished(e -> {
                _mainPane.getChildren().remove(exitNode);
                _changingMainScreen = false;
            });
            transition.play();
        } else {
            _mainPane.getChildren().add(node);
        }
    }

    private void openLeftMenu() {
        _menuPane.setVisible(true);
        _leftMenuPane.setVisible(true);
        AnchorPane.setLeftAnchor(_leftMenuPane, -_leftMenuPane.getWidth());
        TranslateTransition transition = new TranslateTransition(Duration.millis(150), _leftMenuPane);
        transition.setByX(_leftMenuPane.getWidth());
        transition.setOnFinished(e -> {
            AnchorPane.setLeftAnchor(_leftMenuPane, 0.0);
            _leftMenuPane.setTranslateX(0);
        });
        transition.play();
    }

    private void openRightMenu() {
        _menuPane.setVisible(true);
        _rightMenuPane.setVisible(true);
        AnchorPane.setRightAnchor(_rightMenuPane, -_rightMenuPane.getWidth());
        TranslateTransition transition = new TranslateTransition(Duration.millis(150), _rightMenuPane);
        transition.setByX(-_rightMenuPane.getWidth());
        transition.setOnFinished(e -> {
            AnchorPane.setRightAnchor(_rightMenuPane, 0.0);
            _rightMenuPane.setTranslateX(0);
        });
        transition.play();
    }

    private void showDialog(Parent root) {
        _dialogPane.getChildren().clear();
        _dialogPane.getChildren().add(root);
        if (!_dialogPane.isVisible()) {
            _dialogPane.setVisible(true);
        }
        if (root instanceof Pane) {
            root.setVisible(false);
            Platform.runLater(() -> {
                Pane pane = (Pane) root;

                double d = _dialogPane.getHeight() / 2 + pane.getHeight() / 2;
                pane.setTranslateY(d);
                pane.setVisible(true);

                TranslateTransition transition = new TranslateTransition(Duration.millis(150), pane);
                transition.setFromY(d);
                transition.setToY(0);
                transition.playFromStart();
            });
        }
    }

    private void closeDialog() {
        if (_dialogPane.isVisible()) {
            if (!_dialogPane.getChildren().isEmpty()) {
                Node root = _dialogPane.getChildren().get(0);
                if (root instanceof Pane) {
                    Pane pane = (Pane) root;
                    double d = _dialogPane.getHeight() / 2 + pane.getHeight() / 2;

                    TranslateTransition transition = new TranslateTransition(Duration.millis(150), pane);
                    transition.setByY(d);
                    transition.play();
                    transition.setOnFinished(e -> {
                        _dialogPane.setVisible(false);
                    });
                }
            }
        }
    }

    private void setLoadingPaneVisible(boolean visible) {
        _loadingPane.setVisible(visible);
    }

    private void closeMenu() {
        if (_leftMenuPane.isVisible()) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150), _leftMenuPane);
            transition.setByX(-_leftMenuPane.getWidth());
            transition.setOnFinished(e -> {
                _menuPane.setVisible(false);
                AnchorPane.setLeftAnchor(_leftMenuPane, 0.0);
                _leftMenuPane.setTranslateX(0);
                _leftMenuPane.setVisible(false);
            });
            transition.play();
        }

        if (_rightMenuPane.isVisible()) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(150), _rightMenuPane);
            transition.setByX(_rightMenuPane.getWidth());
            transition.setOnFinished(e -> {
                _menuPane.setVisible(false);
                _rightMenuPane.setVisible(false);
                AnchorPane.setRightAnchor(_rightMenuPane, 0.0);
                _rightMenuPane.setTranslateX(0);
            });
            transition.play();
        }
    }

    private void closeSelf() {
        ((Stage) _rootStackPane.getScene().getWindow()).close();
    }

    private void closeSelfAndOpenNew(Parent root) {
        Stage currentStage = (Stage) _rootStackPane.getScene().getWindow();

        Stage newStage = new Stage();
        newStage.setTitle(APP_NAME);
        newStage.setX(currentStage.getX());
        newStage.setY(currentStage.getY());
        newStage.show();
        newStage.setScene(new Scene(root));
        newStage.sizeToScene();
        newStage.setMinWidth(newStage.getWidth());
        newStage.setMinHeight(newStage.getHeight());

        currentStage.close();
    }
}