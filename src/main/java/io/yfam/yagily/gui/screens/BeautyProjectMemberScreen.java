package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.ProjectBus;
import io.yfam.yagily.dto.Member;
import io.yfam.yagily.dto.Role;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.component.BeautyAddCardButton;
import io.yfam.yagily.gui.component.BeautyCardColor;
import io.yfam.yagily.gui.component.BeautyProjectMemberItem;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import java.util.stream.Collectors;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiConstants.APP_NAME;
import static io.yfam.yagily.gui.utils.GuiUtils.loadFXML;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyProjectMemberScreen extends ContextAwareBase implements Initializable {
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
        final double cardWidth = getMemberCardItemWidth();
        final double cardGap = _cardPane.getHgap();
        final double cardPanePadding = _cardPane.getPadding().getLeft() + _cardPane.getPadding().getRight();
        _cardPane.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            int cardsPerRow = (int) ((newValue.doubleValue() - cardPanePadding + cardGap) / (cardWidth + cardGap));
            int justifyGap = (int) ((newValue.doubleValue() - cardPanePadding - cardsPerRow * cardWidth) / (cardsPerRow - 1));
            _cardPane.setHgap(justifyGap);
        });

        // initial actions
        loadProjectMembers();
    }

    private void onPressDeleteItem(BeautyProjectMemberItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete member");
        alert.setContentText("Do you really want to delete?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _cardPane.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteMemberThenRemoveItem(item);
        }
    }

    private void onSelectRoleItem(BeautyProjectMemberItem item) {
        tryUpdateProjectMemberRole(item);
    }

    private double getMemberCardItemWidth() {
        ControllerAndNode<BeautyProjectMemberItem> controllerAndNode
                = loadFXML("/components/beauty-project-member-item.fxml", BeautyProjectMemberItem.class, getContextManager());
        Pane pane = (Pane) controllerAndNode.getNode();
        return pane.getPrefWidth();
    }

    private final ProjectBus _projectBus = new ProjectBus();

    private List<BeautyProjectMemberItem> _items = new LinkedList<>();

    private void loadProjectMembers() {
        launch(new Task<List<Member>>() {
            @Override
            protected List<Member> call() throws Exception {
                return _projectBus.getAllMembers(getContext(ProjectContext.class).getProject());
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                clearMemberItems();
                for (Member member : getValue()) addMemberItem(member);
            }
        });

    }

    private void clearMemberItems() {
        _cardPane.getChildren().clear();
        _items.clear();

        // add add button to card pane
        ControllerAndNode<BeautyAddCardButton> controllerAndNodeAdd =
                loadFXML("/components/beauty-add-card-button.fxml", BeautyAddCardButton.class, getContextManager());
        controllerAndNodeAdd.getController().setOnClick(e -> openTeamMemberChoiceDialog());
        controllerAndNodeAdd.getController().setTitle("Add New Member");
        _cardPane.getChildren().add(controllerAndNodeAdd.getNode());
    }

    private void addMemberItem(Member member) {
        ControllerAndNode<BeautyProjectMemberItem> controllerAndNode
                = loadFXML("/components/beauty-project-member-item.fxml", BeautyProjectMemberItem.class, getContextManager());
        controllerAndNode.getController().setMember(member);
        controllerAndNode.getController().setOnClickClose(e -> onPressDeleteItem(controllerAndNode.getController()));
        controllerAndNode.getController().setOnSelectRole(e -> onSelectRoleItem(controllerAndNode.getController()));
        int cardIndex = (_cardPane.getChildren().size() - 1) % BeautyCardColor.values().length;
        controllerAndNode.getController().setCardColor(BeautyCardColor.values()[cardIndex]);

        _items.add(controllerAndNode.getController());
        _cardPane.getChildren().add(_cardPane.getChildren().size() - 1, controllerAndNode.getNode());

        _subtitleText.setText(String.format("%d members", _cardPane.getChildren().size() - 1));
    }

    private void tryUpdateProjectMemberRole(BeautyProjectMemberItem item) {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _projectBus.updateMemberRole(getContext(ProjectContext.class).getProject(), item.getMember().getUserId(), item.getMember().getRole());
                return null;
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
            }
        });
    }

    private void deleteMemberThenRemoveItem(BeautyProjectMemberItem item) {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _projectBus.removeMember(getContext(ProjectContext.class).getProject(), item.getMember().getUserId());
                return null;
            }

            @Override
            protected void succeeded() {
                int removeIndex = _items.indexOf(item);
                _items.remove(removeIndex);
                _cardPane.getChildren().remove(removeIndex);
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
            }
        });
    }

    private void openTeamMemberChoiceDialog() {
        ControllerAndNode<TeamMemberChoiceScreen> controllerAndNode
                = loadFXML("/screens/team-member-choice-screen.fxml", TeamMemberChoiceScreen.class);
        TeamMemberChoiceScreen teamMemberChoiceScreen = controllerAndNode.getController();
        teamMemberChoiceScreen.exclude(_items.stream().map(it -> it.getMember().getUserId()).collect(Collectors.toList()));

        Stage newStage = new Stage();
        newStage.setTitle(APP_NAME);
        newStage.initStyle(StageStyle.UTILITY);
        newStage.initOwner(((Stage) _cardPane.getScene().getWindow()));
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.setScene(new Scene(controllerAndNode.getNode()));
        newStage.sizeToScene();
        newStage.setMinWidth(newStage.getWidth());
        newStage.setMinHeight(newStage.getHeight());
        newStage.setResizable(false);
        newStage.showAndWait();

        User selectedUser = teamMemberChoiceScreen.getSelectedUser();
        if (selectedUser != null) {
            launch(new Task<Member>() {
                @Override
                protected Member call() throws Exception {
                    _projectBus.addMember(getContext(ProjectContext.class).getProject(), selectedUser.getId(), Role.DEVELOPER);
                    Member member = new Member();
                    member.setUser(selectedUser);
                    member.setRole(Role.DEVELOPER);
                    return member;
                }

                @Override
                protected void failed() {
                    showError(getException().getMessage());
                    getException().printStackTrace();
                }

                @Override
                protected void succeeded() {
                    addMemberItem(getValue());
                }
            });
        }
    }
}
