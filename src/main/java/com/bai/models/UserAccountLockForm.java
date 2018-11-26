package com.bai.models;

public class UserAccountLockForm {
    private int attemptsToLock = 0;


    public UserAccountLockForm() {
    }

    public UserAccountLockForm(int attemptsToLock) {
        this.attemptsToLock = attemptsToLock;
    }

    public int getAttemptsToLock() {
        return attemptsToLock;
    }

    public void setAttemptsToLock(int attemptsToLock) {
        this.attemptsToLock = attemptsToLock;
    }


}
