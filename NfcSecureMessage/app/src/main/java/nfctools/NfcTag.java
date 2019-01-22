package nfctools;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cryptoTools.AESPasswordKey;
import utils.Logging;

public class NfcTag implements Parcelable {

    public static final String TAG = Logging.getTAG(NfcTag.class);

    private boolean isPasswordEncrypted;
    private boolean isValid;
    private boolean isHalfValid;
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
            Log.e(TAG, "invalid tagContent");
            isValid = false; return;
        }
        //then we check if the tag is password encrypted
        if(!processPassword(tagContent, password)){
            Log.e(TAG, "invalid password");
            isValid = false; return;
        }
        String[] splitedTag = decryptedTag.split("\\|");
        if(splitedTag.length < 3 ){
            Log.e(TAG, "invalid splitedTag length");
            isValid = false; return;
        }
        contact = splitedTag[0];
        if(!processHeader(splitedTag[1], false)){
            Log.e(TAG, "invalid header");
            isValid = false; return;
        }
        if(!processKeys(splitedTag[2], false)){
            Log.e(TAG, "invalid Keys length");
            isValid = false; return;
        }
        isHalfValid = true;
        if(splitedTag.length != 5){
            Log.e(TAG, "invalid second splitedTag length");
            isValid = false; return;
        }
        if(!processHeader(splitedTag[3], true)){
            Log.e(TAG, "invalid second header");
            isValid = false; return;
        }
        if(!processKeys(splitedTag[4], true)){
            Log.e(TAG, "invalid second Keys length");
            isValid = false; return;
        }
    }

    public NfcTag(){
        isHalfValid =false;
        isValid = false;
    }

    protected NfcTag(Parcel in) {
        isPasswordEncrypted = in.readByte() != 0;
        isValid = in.readByte() != 0;
        isHalfValid = in.readByte() != 0;
        header = in.createIntArray();
        cesarKey = in.readInt();
        vigenereKey = in.readInt();
        aesKey = in.createByteArray();
        contact = in.readString();
        password = in.readString();
        decryptedTag = in.readString();
        secondHeader = in.createIntArray();
        secondCesarKey = in.readInt();
        secondVigenereKey = in.readInt();
        secondAesKey = in.createByteArray();
    }

    public static final Creator<NfcTag> CREATOR = new Creator<NfcTag>() {
        @Override
        public NfcTag createFromParcel(Parcel in) {
            return new NfcTag(in);
        }

        @Override
        public NfcTag[] newArray(int size) {
            return new NfcTag[size];
        }
    };

    private boolean processKeys(String keys, boolean isSecond) {
        int[] aHeader = isSecond ? secondHeader : header;
        int aCesarKey = 0;
        int aVigenereKey = 0;
        byte[] aAesKey = null;
        int sumOfHeader = aHeader[0] + aHeader[1] + aHeader[2];
        String[] splitedKeys = keys.split(",");
        if(splitedKeys.length != sumOfHeader) return false;
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

    public boolean halfValidate(){
        if(header == null || contact == null || decryptedTag == null){
            isHalfValid = false; return isHalfValid;
        }
        if(header.length != 3 || contact.equals("") || decryptedTag.equals("")){
            isHalfValid = false; return isHalfValid;
        }
        if(header[Encryption.CESAR.ordinal()] == 1 && cesarKey == 0){
            isHalfValid = false; return isHalfValid;
        }
        if(header[Encryption.VIGENERE.ordinal()] == 1 && vigenereKey == 0){
            isHalfValid = false; return isHalfValid;
        }
        if(header[Encryption.AES.ordinal()] == 1 && aesKey == null){
            isHalfValid = false; return isHalfValid;
        }
        String[] splitedTag = decryptedTag.split("\\|");
        if(splitedTag.length < 3){
            isHalfValid = false; return isHalfValid;
        }
        int sumOfHeader = header[0] + header[1] + header[2];
        String[] splitedKeys = splitedTag[2].split(",");
        if(splitedKeys.length != sumOfHeader) {
            isHalfValid = false; return isHalfValid;
        }
        isHalfValid = true; return isHalfValid;
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

    public void halfPopulateDecryptedTag() {
        String response;
        response = "0"; // firs char indicates that it's not encrypted
        response += contact + "|"; //fill contact
        response += header[0] + "," + header[1] + "," + header[2] + "|"; //fill header
        int sumOfHeader = header[0] + header[1] + header[2];
        if(header[Encryption.CESAR.ordinal()] == 1){
            response += cesarKey;
            if(sumOfHeader > 1) response += ",";
        }
        boolean isAes = header[Encryption.AES.ordinal()] == 1;
        if(header[Encryption.VIGENERE.ordinal()] == 1){
            response += vigenereKey;
            if(isAes) response += ",";
        }
        if(isAes){
            response += Base64.encodeToString(aesKey, Base64.DEFAULT);
        }
        decryptedTag = response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isPasswordEncrypted ? 1 : 0));
        dest.writeByte((byte) (isValid ? 1 : 0));
        dest.writeByte((byte) (isHalfValid ? 1 : 0));
        dest.writeIntArray(header);
        dest.writeInt(cesarKey);
        dest.writeInt(vigenereKey);
        dest.writeByteArray(aesKey);
        dest.writeString(contact);
        dest.writeString(password);
        dest.writeString(decryptedTag);
        dest.writeIntArray(secondHeader);
        dest.writeInt(secondCesarKey);
        dest.writeInt(secondVigenereKey);
        dest.writeByteArray(secondAesKey);
    }

    @Override
    public String toString() {
        return "NfcTag{" +
                "isPasswordEncrypted=" + isPasswordEncrypted +
                ", isValid=" + isValid +
                ", isHalfValid=" + isHalfValid +
                ", header=" + Arrays.toString(header) +
                ", cesarKey=" + cesarKey +
                ", vigenereKey=" + vigenereKey +
                ", aesKey=" + Arrays.toString(aesKey) +
                ", contact='" + contact + '\'' +
                ", password='" + password + '\'' +
                ", decryptedTag='" + decryptedTag + '\'' +
                ", secondHeader=" + Arrays.toString(secondHeader) +
                ", secondCesarKey=" + secondCesarKey +
                ", secondVigenereKey=" + secondVigenereKey +
                ", secondAesKey=" + Arrays.toString(secondAesKey) +
                '}';
    }
}
