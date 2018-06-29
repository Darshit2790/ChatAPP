package com.irmsimapp.Encryption;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by darshit on 23/5/17.
 */
public class EncryptionMethods {


    public static String IrregularMD5(String md5Me) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(md5Me.getBytes("UTF-8"));
        return Base64.encodeToString(md.digest(),Base64.DEFAULT);
    }

    public static String NormalMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String EvenOddRevarsal(String password,String targer){
        if(password.length()<=0){
            return null;
        }
        String md5Word = NormalMD5(password+targer);
        StringBuilder originalS = new StringBuilder();
        StringBuilder oddS = new StringBuilder();
        StringBuilder evenS = new StringBuilder();
        int length = md5Word.length();
        for (int i = length - 1; i >= 0; i--) {
            originalS.append(md5Word.charAt(i));
        }
        for (int odd = 0; odd < originalS.length(); ) {
            oddS.append(originalS.charAt(odd));
            odd += 2;
        }
        for (int even = 1; even < originalS.length(); ) {
            evenS.append(originalS.charAt(even));
            even += 2;
        }

        return oddS.toString()+evenS.toString();
    }



}
