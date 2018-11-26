package com.bai.models;

import javax.validation.constraints.Size;

public class PasswordChangeForm {
    @Size(min = 1, max = 50)
    private String oldPassword;
    @Size(min = 1, max = 50)
    private String newPassword;
    @Size(min = 1, max = 50)
    private String newPasswordConfirm;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
