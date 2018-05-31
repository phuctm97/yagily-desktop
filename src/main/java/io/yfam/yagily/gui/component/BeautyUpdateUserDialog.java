package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.TeamBus;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.base.FutureDone;
import io.yfam.yagily.gui.context.MainWindowContext;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;
import static io.yfam.yagily.gui.utils.GuiUtils.showInfo;

public class BeautyUpdateUserDialog extends ContextAwareBase implements Initializable, FutureDone {
    @FXML
    private Label _titleText;

    @FXML
    private Label _generatedPasswordLabel;

    @FXML
    private TextField _usernameText;

    @FXML
    private TextField _generatedPasswordText;

    @FXML
    private ImageView _logoImage;

    @FXML
    private Button _otherImageButton;

    @FXML
    private Button _updateButton;

    @FXML
    private Button _deleteButton;

    private String _logoUrl;

    private Runnable _doneHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _logoImage.setClip(new Circle(_logoImage.getFitWidth() * 0.5, _logoImage.getFitHeight() * 0.5, _logoImage.getFitWidth() * 0.5));

        _otherImageButton.setVisible(true);
        _otherImageButton.setText("Tải lên");
        _logoImage.imageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                _otherImageButton.setText("Tải lên");
                if (!_otherImageButton.isVisible()) {
                    _otherImageButton.setVisible(true);
                }
            } else {
                _otherImageButton.setText("Ảnh khác");
                if (!_logoImage.isHover()) {
                    _otherImageButton.setVisible(false);
                }
            }
        });
    }

    @FXML
    void onBeginHoverImage(MouseEvent event) {
        if (!_otherImageButton.isVisible()) {
            _otherImageButton.setVisible(true);
        }
    }

    @FXML
    void onEndHoverImage(MouseEvent event) {
        if (_otherImageButton.isVisible() && _logoImage.getImage() != null) {
            _otherImageButton.setVisible(false);
        }
    }

    @FXML
    void onPressOtherImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog((Stage) _updateButton.getScene().getWindow());
        if (selectedFile != null) {
            setLogo(selectedFile.getAbsolutePath());
        }
    }

    private void setLogo(String url) {
        if (StringUtils.isBlank(url)) return;

        Image image = null;

        try (InputStream stream = new FileInputStream(url)) {
            image = new Image(stream);
        } catch (IOException ignored) {
            image = null;
        }

        if (image != null && !image.isError()) {
            _logoImage.setImage(image);
            _logoUrl = url;
        }
    }

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressUpdate(ActionEvent event) {
        updateUser();
    }

    @FXML
    void onPressDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete member");
        alert.setContentText("Do you really want to delete?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _deleteButton.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteUser();
        }
    }

    private final TeamBus _teamBus = new TeamBus();

    private User _user;

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
        rerenderUser();
    }

    private void rerenderUser() {
        _titleText.setText(String.format("@%s", _user.getUsername()));
        _usernameText.setText(_user.getUsername());
        setLogo(_user.getAvatarImageUrl());
    }

    private void updateUser() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _user.setUsername(_usernameText.getText());
                _teamBus.updateUser(_user, _logoUrl);
                return null;
            }

            @Override
            protected void succeeded() {
                _result = 1;
                _titleText.setText(String.format("@%s", _user.getUsername()));
                showInfo("Update successfully.");
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void deleteUser() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _teamBus.deleteUser(_user);
                return null;
            }

            @Override
            protected void succeeded() {
                _result = 2;
                closeSelf();
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    @Override
    public void onDone(Runnable handler) {
        _doneHandler = handler;
    }

    private int _result = 0;

    public int getResult() {
        return _result;
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }
}
