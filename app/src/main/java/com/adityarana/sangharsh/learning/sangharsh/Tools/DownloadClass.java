package com.adityarana.sangharsh.learning.sangharsh.Tools;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.adityarana.sangharsh.learning.sangharsh.Model.Video;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;


public class DownloadClass {

    public Context context;
//    @Override
//    protected Boolean doInBackground(String... strings) {
//        Video videoInfo;
//        String key = "_%66&";
//        int one_mb = 1024*1024;
//        try{
//            FileOutputStream outputStream = new FileOutputStream(context.getFilesDir(strings[1], context.MODE_PRIVATE));
//            videoInfo = new Gson().fromJson(strings[0],
//                    Video.class);
//            String category = videoInfo.getCategory();
//            String subcat = videoInfo.getSubcat();
//            String id = videoInfo.getId();
//            String[] ids = id.split(key);
//            FirebaseStorage.getInstance().getReference().
//                    child("content")
//                    .child("categories")
//                    .child(category)
//                    .child("subcat_"+subcat)
//                    .child(ids[1] + "." + ids[2])
//                    .getBytes(one_mb)
//                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                        @Override
//                        public void onSuccess(byte[] bytes) {
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.i("Download", "Failed/" + e.getMessage());
//                    e.printStackTrace();
//                }
//            });
//        } catch (Exception e){
//            Log.i("Download", "Failed/" + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//
//        return null;

    private byte[] genKey(){
        byte[] keyStart = "keyDEVELOPEDofBYsangharshADITYAchhali".getBytes();
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] key = skey.getEncoded();
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DownloadClass(Video videoInfo, Context context) {
        String key = "_%66&";
        int one_mb = 1024 * 1024;

        File file = new File(String.valueOf(context.getDir(videoInfo.getId()+".enc", Context.MODE_PRIVATE)));
        try {
            file.getParentFile().mkdirs();
            FileOutputStream outputStream = new FileOutputStream(file);
            String category = videoInfo.getCategory();
            String subcat = videoInfo.getSubcat();
            String id = videoInfo.getId();
            String[] ids = id.split(key);
            FirebaseStorage.getInstance().getReference().
                    child("content")
                    .child("categories")
                    .child(category)
                    .child("subcat_" + subcat)
                    .child(ids[1] + "." + ids[2])
                    .getBytes(one_mb)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            try {
                                byte[] encryptedData = SimpleCrypto.encrypt(genKey(), "hjhdg0348094".getBytes(), bytes);
                                outputStream.write(encryptedData);
                                Log.i("Encrytion", "done");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Download", "Failed/" + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.i("Download", "Failed/" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class SimpleCrypto {
        public static byte[] encrypt(byte[] key, byte iv[], byte[] clear)
                throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        }

        public static byte[] decrypt(byte[] key, byte[] iv, byte[] encrypted)
                throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        }
    }
}
