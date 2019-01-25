package com.bai.models;

public class LoginHashForm {

    private String username;

    private boolean[] mask = new boolean[16];

    private String[] password = new String[16];

    public LoginHashForm() {
    }

    public LoginHashForm(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[] getPassword() {
        return password;
    }

    public void setPassword(String[] password) {
        this.password = password;
    }

    public boolean[] getMask() {
        return mask;
    }

    public void setMask(boolean[] mask) {
        this.mask = mask;
    }
}
