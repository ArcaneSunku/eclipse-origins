package dev.atomixsoft.solar_eclipse.server.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}
