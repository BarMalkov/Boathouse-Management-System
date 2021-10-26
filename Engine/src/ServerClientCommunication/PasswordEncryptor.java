package ServerClientCommunication;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordEncryptor {

    private static final String myKey = "YOU_SHALL_NOT_PASS";
    private static byte[] key;
    private static SecretKeySpec secretKey;
    private static Cipher cipher;
    static{
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {}
    }

    public static String encrypt(String strToEncrypt) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().
                    encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String strToDecrypt) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }
}
