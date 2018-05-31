package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.TeamBus;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.*;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.base.Store.store;
import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyUserManagementScreen extends ContextAwareBase implements Initializable {
    private final TeamBus _teamBus = new TeamBus();

    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @FXML
    private ScrollPane _cardScrollPane;

    @FXML
    private FlowPane _cardPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // embedded fonts
        _titleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 16.0));
        _subtitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 11.0));

        // anchor size card pane to scroll pane
        _cardPane.prefWidthProperty().bind(_cardScrollPane.widthProperty().subtract(16));

        // justify card to width
        final double cardWidth = getUserCardItemWidth();
        final double cardGap = _cardPane.getHgap();
        final double cardPanePadding = _cardPane.getPadding().getLeft() + _cardPane.getPadding().getRight();
        _cardPane.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            int cardsPerRow = (int) ((newValue.doubleValue() - cardPanePadding + cardGap) / (cardWidth + cardGap));
            int justifyGap = (int) ((newValue.doubleValue() - cardPanePadding - cardsPerRow * cardWidth) / (cardsPerRow - 1));
            _cardPane.setHgap(justifyGap);
        });

        // initial actions
        loadTeamMembers();
    }

    private double getUserCardItemWidth() {
        ControllerAndNode<BeautyUserListItem> controllerAndNode
                = loadFXML("/components/beauty-user-list-item.fxml", BeautyUserListItem.class, getContextManager());
        Pane pane = (Pane) controllerAndNode.getNode();
        return pane.getPrefWidth();
    }

    private void onPressDeleteItem(BeautyUserListItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete member");
        alert.setContentText("Do you really want to delete?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _cardPane.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteMemberThenRemoveMemberItem(item);
        }
    }

    private void onClickItem(BeautyUserListItem item) {
        openUpdateDialog(item);
    }

    private List<BeautyUserListItem> _items = new LinkedList<>();

    private void loadTeamMembers() {
        launch(new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return _teamBus.getAllTeamMembers();
            }

            @Override
            protected void succeeded() {
                clearMemberItems();
                for (User user : getValue()) {
                    if (user.getId() == store().getUser().getId()) continue;
                    addMemberItem(user);
                }
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void clearMemberItems() {
        _items.clear();
        _cardPane.getChildren().clear();

        // add add button to card pane
        ControllerAndNode<BeautyAddCardButton> controllerAndNodeAdd =
                loadFXML("/components/beauty-add-card-button.fxml", BeautyAddCardButton.class, getContextManager());
        controllerAndNodeAdd.getController().setOnClick(e -> openCreateDialog());
        controllerAndNodeAdd.getController().setTitle("Add New Member");
        _cardPane.getChildren().add(controllerAndNodeAdd.getNode());
    }

    private void deleteMemberThenRemoveMemberItem(BeautyUserListItem item) {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _teamBus.deleteUser(item.getUser());
                return null;
            }

            @Override
            protected void succeeded() {
                removeMemberItem(item);
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
            }
        });
    }

    private void addMemberItem(User newUser) {
        ControllerAndNode<BeautyUserListItem> controllerAndNode
                = loadFXML("/components/beauty-user-list-item.fxml", BeautyUserListItem.class, getContextManager());
        controllerAndNode.getController().setUser(newUser);
        controllerAndNode.getController().setOnClickClose(e -> onPressDeleteItem(controllerAndNode.getController()));
        controllerAndNode.getController().setOnClick(e -> onClickItem(controllerAndNode.getController()));
        int cardIndex = (_cardPane.getChildren().size() - 1) % BeautyCardColor.values().length;
        controllerAndNode.getController().setCardColor(BeautyCardColor.values()[cardIndex]);

        _items.add(controllerAndNode.getController());
        _cardPane.getChildren().add(_cardPane.getChildren().size() - 1, controllerAndNode.getNode());

        _subtitleText.setText(String.format("%d members", _cardPane.getChildren().size() - 1));
    }

    private void removeMemberItem(BeautyUserListItem item) {
        int index = _items.indexOf(item);
        _items.remove(index);
        _cardPane.getChildren().remove(index);
    }

    private void openCreateDialog() {
        ControllerAndNode<BeautyCreateUserDialog> controllerAndNode =
                loadFXML("/components/beauty-create-user-dialog.fxml", BeautyCreateUserDialog.class, getContextManager());
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().getCreatedUser() != null) {
                addMemberItem(controllerAndNode.getController().getCreatedUser());
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }

    private void openUpdateDialog(BeautyUserListItem item) {
        ControllerAndNode<BeautyUpdateUserDialog> controllerAndNode =
                loadFXML("/components/beauty-update-user-dialog.fxml", BeautyUpdateUserDialog.class, getContextManager());
        controllerAndNode.getController().setUser(item.getUser());
        controllerAndNode.getController().onDone(() -> {
            if (controllerAndNode.getController().getResult() == 1) {
                item.setUser(controllerAndNode.getController().getUser());
            } else if (controllerAndNode.getController().getResult() == 2) {
                removeMemberItem(item);
            }
        });
        getContext(MainWindowContext.class).showDialog(controllerAndNode.getNode());
    }

    private void navigateToProjectListScreen() {
        ControllerAndNode<BeautyProjectListScreen> controllerAndNode
                = loadFXML("/screens/beauty-project-list-screen.fxml", BeautyProjectListScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }
}

