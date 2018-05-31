package io.yfam.yagily.gui.screens;

import io.yfam.yagily.bus.ProjectBus;
import io.yfam.yagily.gui.base.ContextAwareBase;
import io.yfam.yagily.gui.context.MainWindowContext;
import io.yfam.yagily.gui.context.ProjectContext;
import io.yfam.yagily.gui.utils.ControllerAndNode;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
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
import static io.yfam.yagily.gui.utils.GuiUtils.*;

public class BeautyProjectSettingScreen extends ContextAwareBase implements Initializable {
    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @FXML
    private TextField _keyText;

    @FXML
    private TextField _nameText;

    @FXML
    private ImageView _logoImage;

    @FXML
    private Button _otherImageButton;

    @FXML
    private Button _updateButton;

    @FXML
    private Button _deleteButton;

    private String _logoUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // embedded fonts
        _titleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 16.0));
        _subtitleText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/SourceSansPro-SemiBold.ttf"), 11.0));
        _titleText.setText("Project settings");
        _subtitleText.textProperty().bind(getContext(ProjectContext.class).projectNameProperty());

        _keyText.setText(getContext(ProjectContext.class).getProjectKey());
        _nameText.setText(getContext(ProjectContext.class).getProjectName());
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
        setLogo(getContext(ProjectContext.class).getProjectLogoUrl());
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
        File selectedFile = fileChooser.showOpenDialog((Stage) _keyText.getScene().getWindow());
        if (selectedFile != null) {
            setLogo(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    void onPressDelete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Project Settings");
        alert.setContentText("Do you really want to delete?");
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner((Stage) _logoImage.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteProject();
        }
    }

    @FXML
    void onPressUpdate(ActionEvent event) {
        updateProject();
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

    private final ProjectBus _projectBus = new ProjectBus();

    private void updateProject() {
        ProjectContext projectContext = getContext(ProjectContext.class);

        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _projectBus.updateProject(projectContext.getProject(), _keyText.getText(), _nameText.getText(), _logoUrl, "key,name,logo");
                return null;
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }

            @Override
            protected void succeeded() {
                projectContext.setProjectKey(_keyText.getText());
                projectContext.setProjectName(_nameText.getText());
                projectContext.setProjectLogoUrl(_logoUrl);
                showInfo("Update succeeded.");
            }
        });
    }

    private void deleteProject() {
        launch(new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                _projectBus.deleteProject(getContext(ProjectContext.class).getProject());
                return null;
            }

            @Override
            protected void succeeded() {
                navigateToProjectListScreen();
            }

            @Override
            protected void failed() {
                showError(getException().getMessage());
                getException().printStackTrace();
            }
        });
    }

    private void navigateToProjectListScreen() {
        ControllerAndNode<BeautyProjectListScreen> controllerAndNode =
                loadFXML("/screens/beauty-project-list-screen.fxml", BeautyProjectListScreen.class, getContextManager());
        getContext(MainWindowContext.class).navigate(controllerAndNode.getNode());
    }
}
