package kz.nicnbk.common.service.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility helper class for hashing.
 *
 * Created by magzumov on 19.05.2016.
 */
public class HashUtils {

    /* Hashing algorithms */
    public static final String ALGORITH_MD5 = "MD5";
    public static final String ALGORITH_SHA_1 = "SHA-1";
    public static final String ALGORITH_SHA_256 = "SHA-256";

    public static final String ENCODING_UTF_8 = "UTF-8";


    /**
     * Returns MD5 string hash.
     * Algorithm: MD5, encoding: UTF-8
     *
     * @param message
     * @return
     */
    public static String hashMD5String(String message){
        return hashString(message, ALGORITH_MD5, ENCODING_UTF_8);
    }

    /**
     * Returns string hash using specified algorithm.
     *
     * @param message - input
     * @param algorithm - algorithm
     * @return
     */
    private static String hashString(String message, String algorithm, String encoding) throws IllegalStateException{
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(message.getBytes(encoding));

            return convertByteArrayToHexString(hashedBytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not generate hash from string: " + message +
                    " (Algo:" + algorithm + ", enc: " + encoding + ")");
        }
    }

    /**
     * Returns string representation of byte array.
     *
     * @param arrayBytes - bytes
     * @return - string
     */
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
