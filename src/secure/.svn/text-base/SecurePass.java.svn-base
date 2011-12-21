package secure;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for securely storing a password using a cryptographic has
 * All methods called in a static context. Do NOT create an instance of this class
 */
public abstract class SecurePass {
    
    /**
     * Function to generate a hex SHA-256 hash of a string
     * @param pass - requires non-null String
     * @return - hex SHA-256 hash of the string given in the argument to the function
     */
    public static String hash(String pass) {
        String result = null;
        if (pass == null) {
            return result;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            result = new BigInteger(1, md.digest(pass.getBytes())).toString(16);
            if (result.length() % 2 != 0) {
                result = "0" + result;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
