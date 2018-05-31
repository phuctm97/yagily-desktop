package io.yfam.yagily.dto;

public class Member {
    private int _userId;
    private User _user;
    private Role _role;

    public Member() {
        _userId = 0;
    }

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
        if (_user != null) _userId = _user.getId();
        else _userId = 0;
    }

    public Role getRole() {
        return _role;
    }

    public void setRole(Role role) {
        _role = role;
    }

    public int getUserId() {
        return _userId;
    }

    public void setUserId(int userId) {
        _userId = userId;
    }
}
