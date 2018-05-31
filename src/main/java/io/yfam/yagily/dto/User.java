package io.yfam.yagily.dto;

public class User {
    private int _id;
    private String _username;
    private String _password;
    private String _avatarImageUrl;
    private Permission _permission;
    private boolean _isOwner;

    public User() {
        _isOwner = false;
        _permission = Permission.MEMBER;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public Permission getPermission() {
        return _permission;
    }

    public void setPermission(Permission permission) {
        _permission = permission;
    }

    public boolean isOwner() {
        return _isOwner;
    }

    public void setOwner(boolean owner) {
        _isOwner = owner;
    }

    public String getAvatarImageUrl() {
        return _avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        _avatarImageUrl = avatarImageUrl;
    }
}
