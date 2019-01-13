package cryptoTools;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import utils.Logging;

public class AppPassword {
    public static final String PASSWORDALIAS = "PASSWORDALIAS";
    private static final String TAG = Logging.getTAG(AppPassword.class);
    private static byte[] encryptedText = new byte[0];

    public AppPassword(){}

    public static boolean isCreated(){
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore.containsAlias(PASSWORDALIAS);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean create(String password){
        try {
            SecretKey sk = AESEnCryptor.simpleGenerateKey(PASSWORDALIAS);
            encryptedText = new AESEnCryptor()
                    .encryptTextWithKey(password, sk);
            Log.i(TAG, "creating AppPassword: " + Base64.encodeToString(encryptedText, Base64.DEFAULT));
            return true;
        } catch ( NoSuchAlgorithmException |
                IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "Error while creating password ... " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "Error while creating password ... " + e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            Log.e(TAG, "Error while creating password ... " + e.getMessage(), e);
        }
        return false;
    }

    public static byte[] getEncryptedText(){
        return encryptedText;
    }

    public static boolean isMatchingAppPassword(String password, String savedPassword){
        try {
            AESDeCryptor aesDeCryptor = new AESDeCryptor();
            SecretKey secretKey = aesDeCryptor.getSecretKey(PASSWORDALIAS);
            AESEnCryptor aesEnCryptor = new AESEnCryptor();
            final byte[] encryptedPassword = aesEnCryptor.encryptTextWithKey(password, secretKey);
            String encryptedpasswordStr = Base64.encodeToString(encryptedPassword, Base64.DEFAULT);
            Log.i(TAG, "encrypted password: " + encryptedpasswordStr);
            Log.i(TAG, "saved Password: " + savedPassword);
            return savedPassword.trim().equals(encryptedpasswordStr.trim());
        } catch (CertificateException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (KeyStoreException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (UnrecoverableEntryException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (BadPaddingException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            Log.e(TAG, "Error while matching password ... " + e.getMessage(), e);
        }
        return false;
    }
}
