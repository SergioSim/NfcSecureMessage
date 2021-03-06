package cryptoTools;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import utils.Logging;

/**
 ______        _____                  _
 |  ____|      / ____|                | |
 | |__   _ __ | |     _ __ _   _ _ __ | |_ ___  _ __
 |  __| | '_ \| |    | '__| | | | '_ \| __/ _ \| '__|
 | |____| | | | |____| |  | |_| | |_) | || (_) | |
 |______|_| |_|\_____|_|   \__, | .__/ \__\___/|_|
 __/ | |
 |___/|_|
 */
public class AESEnCryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String SIMPLETRANSFORMATION = "AES/ECB/PKCS7Padding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String VERYSIMPLETRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String TAG = Logging.getTAG(AESEnCryptor.class);

    private byte[] encryption;
    private byte[] iv;

    public AESEnCryptor() {
    }

    public byte[] encryptText(final String alias, final String textToEncrypt)
            throws NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, BadPaddingException,
            IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        iv = cipher.getIV();

        return (encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    public byte[] encryptTextWithKey(final String textToEncrypt, SecretKey secretKey )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException,
            IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance(SIMPLETRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        iv = cipher.getIV();

        return (encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    public static String verySimpleEncryptData(byte[] secretKey, String message)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        Cipher cipher = null;
        cipher = Cipher.getInstance(SIMPLETRANSFORMATION);
        SecretKey key = new SecretKeySpec(secretKey, 0, secretKey.length, VERYSIMPLETRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        return keyGenerator.generateKey();
    }

    public byte[] getEncryption() {
        return encryption;
    }

    public byte[] getIv() {
        return iv;
    }

    public static SecretKey simpleGenerateKey(final String alias) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, NoSuchProviderException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setKeySize(256)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(false)
                .build());

        return keyGenerator.generateKey();
    }

    public static byte[] verySimpleGenerateKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("AES").generateKey().getEncoded();
    }
}
