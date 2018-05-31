package io.yfam.yagily.gui.context;

import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.base.Context;

public class UserContext implements Context {
    private User _user;

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
    }
}
