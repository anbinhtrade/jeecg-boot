package org.jeecg.modules.system.controller;

import org.jeecg.common.util.PasswordUtil;

public class TestMainPassword {
    // create main method
    public static void main(String[] args) {
        String password_jeecg = PasswordUtil.encrypt("jeecg", "jeecg@123", "migiyjow");
        String password_admin = PasswordUtil.encrypt("admin", "admin@123", "rcgtegih");
        System.out.println("Password Admin: " + password_admin);
        System.out.println("Password Jeecg: " + password_jeecg);
    }
}
