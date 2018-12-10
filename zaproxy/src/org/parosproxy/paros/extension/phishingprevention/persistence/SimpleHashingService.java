package org.parosproxy.paros.extension.phishingprevention.persistence;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;

/**
 * Algorithm taken of: https://stackoverflow.com/questions/2860943/how-can-i-hash-a-password-in-java
 */
public class SimpleHashingService implements PasswordHashingService {

    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    @Override
    public String getHash(String password) {
        try {
            byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
            return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean check(String password, String stored){
        try {
            String[] saltAndHash = stored.split("\\$");
            if (saltAndHash.length != 2) {
                throw new IllegalStateException(
                    "The stored password must have the form 'salt$hash'");
            }
            String hashOfInput = hash(password, Base64.decodeBase64(saltAndHash[0]));
            return hashOfInput.equals(saltAndHash[1]);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String hash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");

        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen));

        return Base64.encodeBase64String(key.getEncoded());
    }
}