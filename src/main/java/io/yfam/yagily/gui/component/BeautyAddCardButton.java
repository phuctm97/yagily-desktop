package io.yfam.yagily.gui.component;

import io.yfam.yagily.gui.base.ContextAwareBase;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;


public class BeautyAddCardButton extends ContextAwareBase {
    @FXML
    private Label _titleText;

    @FXML
    void onClick(MouseEvent event) {
        if (_onClick != null) _onClick.handle(event);
    }

    private EventHandler<MouseEvent> _onClick;

    public EventHandler<MouseEvent> getOnClick() {
        return _onClick;
    }

    public void setOnClick(EventHandler<MouseEvent> onClick) {
        _onClick = onClick;
    }

    public void setTitle(String title) {
        _titleText.setText(title);
    }
}
