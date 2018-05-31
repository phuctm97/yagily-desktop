package io.yfam.yagily.gui.component;

import io.yfam.yagily.bus.ProjectBus;
import io.yfam.yagily.dto.Project;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static io.yfam.yagily.gui.base.Store.store;
import static io.yfam.yagily.gui.utils.ConcurrentUtils.launch;
import static io.yfam.yagily.gui.utils.GuiUtils.showError;

public class BeautyCreateProjectDialog extends ContextAwareBase implements Initializable, FutureDone {
    @FXML
    private TextField _keyText;

    @FXML
    private TextField _nameText;

    @FXML
    private ImageView _logoImage;

    @FXML
    private Button _otherImageButton;

    @FXML
    private Button _createButton;

    private String _logoUrl;

    private Runnable _doneHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        createProject();
    }

    @FXML
    void onPressOtherImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File selectedFile = fileChooser.showOpenDialog((Stage) _keyText.getScene().getWindow());
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

    @FXML
    void onClickClose(MouseEvent event) {
        closeSelf();
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

    private void closeSelf() {
        getContext(MainWindowContext.class).closeDialog();
        if (_doneHandler != null) _doneHandler.run();
    }

    private final ProjectBus _projectBus = new ProjectBus();

    private Project _createdProject;

    public Project getCreatedProject() {
        return _createdProject;
    }

    private void createProject() {
        launch(new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                return _projectBus.createProject(store().getUser().getId(), _keyText.getText(), _nameText.getText(), _logoUrl);
            }

            @Override
            protected void succeeded() {
                _createdProject = getValue();
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
}
