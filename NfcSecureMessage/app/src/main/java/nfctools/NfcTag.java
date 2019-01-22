package nfctools;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cryptoTools.AESPasswordKey;
import utils.Logging;

public class NfcTag {

    public static final String TAG = Logging.getTAG(NfcTag.class);

    private boolean isPasswordEncrypted;
    private boolean isValid;
    private int[] header; //example [1,0,1] for cesar + aes encryption
    private int cesarKey;
    private int vigenereKey;
    private byte[] aesKey;
    private String contact;
    private String password;
    private String decryptedTag;

    private int[] secondHeader;
    private int secondCesarKey;
    private int secondVigenereKey;
    private byte[] secondAesKey;

    public NfcTag(String tagContent, String password){
        this.password = password;
        //First we check if the tag is not null;
        if(tagContent == null || tagContent.equals("")){
            isValid = false; return;
        }
        //then we check if the tag is password encrypted
        if(!processPassword(tagContent, password)){
            isValid = false; return;
        }
        String[] splitedTag = decryptedTag.split("\\|");
        contact = splitedTag[0];
        if(!processHeader(splitedTag[1], false)){
            isValid = false; return;
        }
        if(!processKeys(splitedTag[2], false)){
            isValid = false; return;
        }
        if(!processHeader(splitedTag[3], true)){
            isValid = false; return;
        }
        if(!processKeys(splitedTag[4], true)){
            isValid = false; return;
        }
    }

    private boolean processKeys(String keys, boolean isSecond) {
        int[] aHeader = isSecond ? secondHeader : header;
        int aCesarKey = 0;
        int aVigenereKey = 0;
        byte[] aAesKey = null;
        int sumOfHeader = aHeader[0] + aHeader[1] + aHeader[2];
        String[] splitedKeys = keys.split(",");
        if(splitedKeys.length == sumOfHeader) return false;
        try {
            int i = 0; int z = 0;
            for(String key : splitedKeys){
                if(aHeader[Encryption.CESAR.ordinal()] == 1 && i == 0){
                    aCesarKey = Integer.parseInt(key);
                    i++; continue;
                }
                if(aHeader[Encryption.VIGENERE.ordinal()] == 1 && z == 0) {
                    aVigenereKey = Integer.parseInt(key);
                    z++; continue;
                }
                if(aHeader[Encryption.AES.ordinal()] == 1) {
                    aAesKey = Base64.decode(key, Base64.DEFAULT);
                    break;
                }
            }
            if(!isSecond){
                cesarKey = aCesarKey;
                vigenereKey = aVigenereKey;
                aesKey = aAesKey;
            }else{
                secondCesarKey = aCesarKey;
                secondVigenereKey = aVigenereKey;
                secondAesKey = aAesKey;
            }
        }catch (NumberFormatException nfe){
            Log.e(TAG, "invalid keys format ...");
            return false;
        }
        return true;
    }

    private boolean processHeader(String splitedTag, boolean isSecond) {
        String[] strHeader = splitedTag.split(",");
        if(strHeader.length != 3) return false;
        try {
            int[] aheader = new int[]{
                    Integer.parseInt(strHeader[Encryption.CESAR.ordinal()]),
                    Integer.parseInt(strHeader[Encryption.VIGENERE.ordinal()]),
                    Integer.parseInt(strHeader[Encryption.AES.ordinal()])};
            if(!isSecond){
                header = aheader;
            }else{
                secondHeader = aheader;
            }
        }catch (NumberFormatException nfe){
            Log.e(TAG, "invalid header format ...");
            return false;
        }
        return true;
    }

    private boolean processPassword(String tagContent, String password) {
        try {
            isPasswordEncrypted = Integer.parseInt(tagContent.substring(0,1)) == 1;
        }catch (NumberFormatException nfe){
            Log.e(TAG, "password null or void ...");
            return false;
        }
        decryptedTag = tagContent.substring(1);
        if(isPasswordEncrypted) {
            //then we check the validity of the tag
            if (password == null || password.equals("")) {
                Log.e(TAG, "password null or void ...");
                return false;
            } else {
                try {
                    decryptedTag = new AESPasswordKey().decrypt(tagContent.substring(1), password.getBytes());
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException
                        | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception while decrypting tag ... " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validate(){
        if(header == null || secondHeader == null || contact == null || decryptedTag == null){
            isValid = false; return isValid;
        }
        if(header.length != 3 || secondHeader.length != 3 || contact.equals("") || decryptedTag.equals("")){
            isValid = false; return isValid;
        }
        if((header[Encryption.CESAR.ordinal()] == 1 && cesarKey == 0) || (secondHeader[Encryption.CESAR.ordinal()] == 1 && secondCesarKey == 0)){
            isValid = false; return isValid;
        }
        if((header[Encryption.VIGENERE.ordinal()] == 1 && vigenereKey == 0) || (secondHeader[Encryption.VIGENERE.ordinal()] == 1 && secondVigenereKey == 0)){
            isValid = false; return isValid;
        }
        if((header[Encryption.AES.ordinal()] == 1 && aesKey == null) || (secondHeader[Encryption.AES.ordinal()] == 1 && secondAesKey == null)){
            isValid = false; return isValid;
        }
        String[] splitedTag = decryptedTag.split("\\|");
        if(splitedTag.length != 5){
            isValid = false; return isValid;
        }
        int sumOfHeader = header[0] + header[1] + header[2];
        int secondSumOfHeader = secondHeader[0] + secondHeader[1] + secondHeader[2];
        String[] splitedKeys = splitedTag[2].split(",");
        String[] secondSplitedKeys = splitedTag[4].split(",");
        if(splitedKeys.length != sumOfHeader || secondSplitedKeys.length != secondSumOfHeader) {
            isValid = false; return isValid;
        }
        isValid = true; return isValid;
    }

    public static String getTAG() {
        return TAG;
    }

    public boolean isPasswordEncrypted() {
        return isPasswordEncrypted;
    }

    public void setPasswordEncrypted(boolean passwordEncrypted) {
        isPasswordEncrypted = passwordEncrypted;
    }

    public boolean isValid() {
        return isValid;
    }

    private void setValid(boolean valid) {
        isValid = valid;
    }

    public int[] getHeader() {
        return header;
    }

    public void setHeader(int[] header) {
        this.header = header;
    }

    public int getCesarKey() {
        return cesarKey;
    }

    public void setCesarKey(int cesarKey) {
        this.cesarKey = cesarKey;
    }

    public int getVigenereKey() {
        return vigenereKey;
    }

    public void setVigenereKey(int vigenereKey) {
        this.vigenereKey = vigenereKey;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public void setAesKey(byte[] aesKey) {
        this.aesKey = aesKey;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDecryptedTag() {
        return decryptedTag;
    }

    public void setDecryptedTag(String decryptedTag) {
        this.decryptedTag = decryptedTag;
    }
}
