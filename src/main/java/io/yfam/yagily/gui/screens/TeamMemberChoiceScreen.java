package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.TeamBus;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.component.SimpleUserListCell;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.base.Store.store;
import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class TeamMemberChoiceScreen implements Initializable {
    private final TeamBus _teamBus = new TeamBus();

    @FXML
    private ListView<User> _listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _listView.setCellFactory(e -> new SimpleUserListCell());
        _listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    void onPressSelect(ActionEvent event) {
        _selectedUser = _listView.getSelectionModel().getSelectedItem();
        ((Stage) _listView.getScene().getWindow()).close();
    }

    private User _selectedUser;

    public User getSelectedUser() {
        return _selectedUser;
    }

    public void exclude(List<Integer> idList) {
        launch(new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return _teamBus.getAllTeamMembers();
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
            }

            @Override
            protected void succeeded() {
                _listView.getItems().clear();
                for (User user : getValue()) {
                    if (user.getId() != store().getUser().getId() &&
                            !idList.contains(user.getId())) {
                        _listView.getItems().add(user);
                    }
                }
            }
        });
    }
}
