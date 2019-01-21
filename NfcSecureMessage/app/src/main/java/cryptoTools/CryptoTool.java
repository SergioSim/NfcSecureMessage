package cryptoTools;

public class CryptoTool
{
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static String encrypt(String plainText, int shiftKey)
    {
        plainText = plainText.toLowerCase();
        String cipherText = "";
        for (int i = 0; i < plainText.length(); i++)
        {
            char replaceVal = plainText.charAt(i);
            int charPosition = ALPHABET.indexOf(replaceVal);
            if(charPosition != -1) {
                int keyVal = (shiftKey + charPosition) % 26;
                replaceVal = ALPHABET.charAt(keyVal);
            }

            cipherText += replaceVal;
        }
        return cipherText;
    }

    public static String decrypt(String cipherText, int shiftKey)
    {
        cipherText = cipherText.toLowerCase();
        String plainText = "";
        for (int i = 0; i < cipherText.length(); i++)
        {
            char replaceVal = cipherText.charAt(i);
            int charPosition = ALPHABET.indexOf(replaceVal);
            if(charPosition != -1) {
                int keyVal = (charPosition - shiftKey) % 26;
                if (keyVal < 0) {
                    keyVal = ALPHABET.length() + keyVal;
                }
                replaceVal = ALPHABET.charAt(keyVal);
            }
            plainText += replaceVal;
        }
        return plainText;
    }

}