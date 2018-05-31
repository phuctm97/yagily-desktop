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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;
import static io.yfam.yagily.gui.utils.GuiUtils.showInfo;

public class BeautyCreateUserDialog extends ContextAwareBase implements Initializable, FutureDone {
    @FXML
    private TextField _usernameText;

    @FXML
    private TextField _generatedPasswordText;

    @FXML
    private Button _createButton;

    @FXML
    private ImageView _logoImage;

    @FXML
    private Button _otherImageButton;

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
    void onPressCreate(ActionEvent event) {
        createMember();
    }

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
    }

    @FXML
    void onPressOtherImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog((Stage) _createButton.getScene().getWindow());
        if (selectedFile != null) {
            setLogo(selectedFile.getAbsolutePath());
        }
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


    private final TeamBus _teamBus = new TeamBus();

    private User _createdUser;

    public User getCreatedUser() {
        return _createdUser;
    }

    private void createMember() {
        launch(new Task<User>() {
            @Override
            protected User call() throws Exception {
                return _teamBus.createUser(_usernameText.getText(), _logoUrl);
            }

            @Override
            protected void succeeded() {
                _generatedPasswordText.setText(getValue().getPassword());
                _createButton.setDisable(true);
                _createdUser = getValue();
                showInfo("Created successfully.");
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }

    @Override
    public void onDone(Runnable handler) {
        _doneHandler = handler;
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
}
