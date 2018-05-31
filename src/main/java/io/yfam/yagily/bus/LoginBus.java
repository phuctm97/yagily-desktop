package io.yfam.yagily.bus;

import io.yfam.yagily.bus.exceptions.CheckedException;
import io.yfam.yagily.dao.UserDao;
import io.yfam.yagily.dto.User;

public class LoginBus {
    private final UserDao _userDao = new UserDao();

    public User login(String username, String password) {
        User user = _userDao.getByUsername(username);
        if (user == null || !user.getPassword().equals(password))
            throw new CheckedException("Wrong password or username!");
        return user;
    }
}
