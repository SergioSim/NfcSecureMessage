package crypto;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AppPassword {
    public static final String PASSWORDALIAS = "PASSWORDALIAS";
    public static final String TAG = AppPassword.class.getSimpleName();
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
            encryptedText = new AESEnCryptor()
                    .encryptText(PASSWORDALIAS, password);
            Log.i(TAG, Base64.encodeToString(encryptedText, Base64.DEFAULT));
            return true;
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "Error while creating password ... " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
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
            Log.i("CheckingPasswords1", encryptedpasswordStr);
            Log.i("CheckingPasswords2", savedPassword);
            return savedPassword.equals(encryptedpasswordStr);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }
}
