package io.yfam.yagily.bus;

import io.yfam.yagily.dao.UserDao;
import io.yfam.yagily.dto.Permission;
import io.yfam.yagily.dto.User;
import io.yfam.yagily.gui.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Random;

import static io.yfam.yagily.dao.utils.DaoConstants.IMAGE_DIRECTORY;

public class TeamBus {
    private final UserDao _userDao = new UserDao();

    public void updateUser(User user, String avatar) {
        User oldUser = _userDao.getByUsername(user.getUsername());
        if (oldUser != null) {
            if (user.getId() != oldUser.getId()) {
                throw new RuntimeException("Username has been already used.");
            }
        }

        String toSaveAvatarPath = StringUtils.isNotBlank(avatar) ? String.format("%s/%s", IMAGE_DIRECTORY, IOUtils.generateUniqueFileName(avatar)) : null;
        if (StringUtils.isNotBlank(toSaveAvatarPath)) {
            IOUtils.copyFile(avatar, toSaveAvatarPath);
            user.setAvatarImageUrl(toSaveAvatarPath);
        } else {
            if (oldUser != null) {
                user.setAvatarImageUrl(oldUser.getAvatarImageUrl());
            }
        }

        _userDao.update(user);
    }

    public User createUser(String username, String avatar) {
        if (_userDao.existByUsername(username)) {
            throw new RuntimeException("Username has been already used.");
        }
        String toSaveAvatarPath = StringUtils.isNotBlank(avatar) ? String.format("%s/%s", IMAGE_DIRECTORY, IOUtils.generateUniqueFileName(avatar)) : null;
        if (StringUtils.isNotBlank(toSaveAvatarPath)) {
            IOUtils.copyFile(avatar, toSaveAvatarPath);
        }

        User user = new User();
        user.setPermission(Permission.MEMBER);
        user.setOwner(false);
        user.setUsername(username);
        user.setPassword(generatePassword());
        user.setAvatarImageUrl(toSaveAvatarPath);
        _userDao.insert(user);

        return user;
    }

    public List<User> getAllTeamMembers() {
        return _userDao.getAll();
    }

    public void deleteUser(User user) {
        _userDao.delete(user.getId());
    }

    private String generatePassword() {
        String chars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        int length = 6;

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }
}
