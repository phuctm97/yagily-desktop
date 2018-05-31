package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.User;
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
import java.util.List;
import java.util.ResourceBundle;

public class BeautyUserListItem extends ContextAwareBase implements Initializable {
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
        _logoImage.setClip(new Circle(_logoImage.getFitWidth() * 0.5, _logoImage.getFitHeight() * 0.5, _logoImage.getFitWidth() * 0.5));
    }

    @FXML
    void onClick(MouseEvent event) {
        if (_onClick != null) _onClick.handle(event);
    }


    @FXML
    void onClickClose(MouseEvent event) {
        if (_onClickClose != null) _onClickClose.handle(event);
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

    private EventHandler<MouseEvent> _onClickClose;

    private User _user;

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
        rerenderUser();
    }

    public void setOnClick(EventHandler<MouseEvent> handler) {
        _onClick = handler;
    }

    public void setOnClickClose(EventHandler<MouseEvent> onClickClose) {
        _onClickClose = onClickClose;
    }

    private void rerenderUser() {
        setAvatar(_user.getAvatarImageUrl(), generateBackupShortName(_user.getUsername()));
        setName(_user.getUsername());
        setUsername(_user.getUsername());
    }

    private static final List<Character> VOWELS = Arrays.asList('o', 'a', 'e', 'u', 'i');

    private String generateBackupShortName(String username) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < username.length(); i++) {
            if (!VOWELS.contains(username.charAt(i))) {
                builder.append(username.charAt(i));
            }
            if (builder.length() == 2) break;
        }

        if (builder.length() == 1) {
            builder.append(builder.charAt(0));
        } else if (builder.length() == 0) {
            builder.append(username.charAt(0));
            builder.append(username.charAt(0));
        }

        return builder.toString().toUpperCase();
    }

    private void setAvatar(String path, String backupKey) {
        if (StringUtils.isBlank(path)) return;

        Image image = null;

        try (InputStream stream = new FileInputStream(path)) {
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

    private void setName(String name) {
        _titleText.setText(name);
    }

    private void setUsername(String key) {
        _subtitleText.setText(String.format("@%s", key));
    }
}
