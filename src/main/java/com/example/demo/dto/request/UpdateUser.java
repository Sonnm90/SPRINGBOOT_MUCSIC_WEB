package com.example.demo.dto.request;

public class UpdateUser {
    private String name;
    private String avatar;
    private String password;

    public UpdateUser() {
    }

    public UpdateUser(String name, String avatar, String password) {
        this.name = name;
        this.avatar = avatar;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
