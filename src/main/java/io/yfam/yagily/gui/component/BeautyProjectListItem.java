package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.Project;
import io.yfam.yagily.gui.base.ContextAwareBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class BeautyProjectListItem extends ContextAwareBase implements Initializable {
    @FXML
    private VBox _rootPane;

    @FXML
    private Circle _logoCircle;

    @FXML
    private Label _logoText;

    @FXML
    private ImageView _logoImage;

    @FXML
    private Label _titleText;

    @FXML
    private Label _subtitleText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (_cardColor == null) setCardColor(BeautyCardColor.ONE);
    }

    @FXML
    void onClick(MouseEvent event) {
        if (_onClick != null) _onClick.handle(event);
    }

    private BeautyCardColor _cardColor;

    public BeautyCardColor getCardColor() {
        return _cardColor;
    }

    public void setCardColor(BeautyCardColor cardColor) {
        _cardColor = cardColor;
        onCardColorChange();
    }

    private void onCardColorChange() {
        String regexBack = String.format("card-item-(%s)",
                StringUtils.join(Arrays.stream(BeautyCardColor.values()).map(s -> s.name().toLowerCase()).toArray(), '|'));
        _rootPane.getStyleClass().removeIf(e -> e.matches(regexBack));
        _rootPane.getStyleClass().add(String.format("card-item-%s", _cardColor.name().toLowerCase()));

        String regexText = String.format("text-item-(%s)",
                StringUtils.join(Arrays.stream(BeautyCardColor.values()).map(s -> s.name().toLowerCase()).toArray(), '|'));
        _logoText.getStyleClass().removeIf(e -> e.matches(regexBack));
        _logoText.getStyleClass().add(String.format("text-item-%s", _cardColor.name().toLowerCase()));
    }

    private EventHandler<MouseEvent> _onClick;

    private Project _project;

    public void setProject(Project project) {
        _project = project;
        if (_project != null) {
            rerenderProject();
        }
    }

    public Project getProject() {
        return _project;
    }

    public void setOnClick(EventHandler<MouseEvent> handler) {
        _onClick = handler;
    }

    private void rerenderProject() {
        setIcon(_project.getLogoUrl(), _project.getKey());
        setKey(_project.getKey());
        setTitle(_project.getName());
    }

    private void setIcon(String path, String backupKey) {
        Image image = null;

        try (InputStream stream = new FileInputStream(path == null ? "" : path)) {
            image = new Image(stream);
        } catch (IOException ignored) {
            image = null;
        }

        if (image == null || image.isError()) {
            _logoImage.setVisible(false);
            _logoText.setVisible(true);
            _logoText.setText(backupKey);
        } else {
            _logoText.setVisible(false);
            _logoImage.setImage(image);
            _logoImage.setVisible(true);
        }
    }

    private void setTitle(String title) {
        _titleText.setText(title);
    }

    private void setKey(String key) {
        _subtitleText.setText(String.format("@%s", key));
    }
}
