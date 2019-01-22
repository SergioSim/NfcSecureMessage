package cryptoTools;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import utils.Logging;

/**
 _____        _____                  _
 |  __ \      / ____|                | |
 | |  | | ___| |     _ __ _   _ _ __ | |_ ___  _ __
 | |  | |/ _ \ |    | '__| | | | '_ \| __/ _ \| '__|
 | |__| |  __/ |____| |  | |_| | |_) | || (_) | |
 |_____/ \___|\_____|_|   \__, | .__/ \__\___/|_|
 __/ | |
 |___/|_|
 */
public class AESDeCryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String SIMPLETRANSFORMATION = "AES/ECB/PKCS7Padding";
    private static final String VERYSIMPLETRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static final String TAG = Logging.getTAG(AESDeCryptor.class);

    private KeyStore keyStore;

    public AESDeCryptor() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException {
        initKeyStore();
    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    public String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    public static String simpleDecryptData(byte[] secretKey, String encryptedData)
            throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = null;
        cipher = Cipher.getInstance(VERYSIMPLETRANSFORMATION);
        SecretKey key = new SecretKeySpec(secretKey, 0, secretKey.length, VERYSIMPLETRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decode = Base64.decode(encryptedData, Base64.DEFAULT);
        Log.i(TAG, "This is not a decoding problem...");
        return new String(cipher.doFinal(decode), "UTF-8");
    }

    public SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }
}