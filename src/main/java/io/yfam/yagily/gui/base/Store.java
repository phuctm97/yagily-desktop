package io.yfam.yagily.gui.base;

import io.yfam.yagily.dto.User;

public final class Store {
    private final static Store INSTANCE = new Store();

    public static Store getInstance() {
        return INSTANCE;
    }

    public static Store store() {
        return INSTANCE;
    }

    private Store() {
//        _user = new User();
//        _user.setId(101);
    }

    private User _user;

    public boolean isLoggedIn() {
        return _user != null;
    }

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
    }

    public void reset() {
        _user = null;
    }
}
