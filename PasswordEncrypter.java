import java.security.*;
import java.math.*;

public class PasswordEncrypter {
    MessageDigest md;

    PasswordEncrypter() {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
    }

    String returnHash(String input) {

        byte[] messageDigest = md.digest(input.getBytes());
        // Convert byte array into signum representation
        BigInteger num = new BigInteger(1, messageDigest);
        // Convert message digest into hex value
        String hashtext = num.toString(16);

        // Add preceding 0s to make it 32 bit
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

}
