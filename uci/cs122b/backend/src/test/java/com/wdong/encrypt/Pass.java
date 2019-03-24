package com.wdong.encrypt;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Test;

public class Pass {
    @Test
    public void test() {
        StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();

        String password = strongPasswordEncryptor.encryptPassword("a");

        boolean success = strongPasswordEncryptor.checkPassword("a", password);

        assert success;
    }
}
