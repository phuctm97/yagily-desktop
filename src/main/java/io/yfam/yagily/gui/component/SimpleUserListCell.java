package io.yfam.yagily.gui.component;

import io.yfam.yagily.dto.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

public class SimpleUserListCell extends ListCell<User> {
    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            Label label = new Label(item.getUsername());
            label.setFont(new Font("Tahoma", 18));
            setGraphic(label);
        }
    }
}
