package com.shailesh;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class SaveFile {
    enum Mode {
        INIT_KEY_PAIR,
        DECRYPT_FILE,
        ENCRYPT_FILE
    }

    private static final Mode mode = Mode.DECRYPT_FILE;
    private static final String path = "D:/KeyPair";
    private static final String fileToDecrypt = "enc1673738763300.txt";
    private static final String fileToEncrypt = "src/main/resources/DailyDiary.txt";
    private static final String encryptedFileLocation = "src/main/java/com/shailesh/files/";

    public static void main(String args[]) throws Exception {
        switch (mode) {
            case INIT_KEY_PAIR -> IntKeyPair();
            case ENCRYPT_FILE ->  EncryptFile();
            case DECRYPT_FILE ->  DecryptFile();
        }
    }

    private static void DecryptFile() throws Exception {
        KeyPair pair = LoadKeyPair(path);
        PrivateKey privateKey = pair.getPrivate();
        String readFile =  encryptedFileLocation + fileToDecrypt;

        byte[] encryptedFileBytes = Files.readAllBytes(Paths.get(readFile));
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedFileBytes = decryptCipher.doFinal(encryptedFileBytes);
        System.out.println(new String(decryptedFileBytes, StandardCharsets.UTF_8));
    }

    private static void EncryptFile() throws Exception {
        KeyPair pair = LoadKeyPair(path);
        Long epoch = new Date().getTime();
        String outFile = encryptedFileLocation + "enc" + epoch + ".txt";
        byte[] fileBytes = Files.readAllBytes(Paths.get(fileToEncrypt));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());

        cipher.update(fileBytes);
        byte[] encryptedBytes = cipher.doFinal();
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(encryptedBytes);
        fos.close();
    }

    private static void dumpKeyPair(KeyPair keyPair) {
        PublicKey pub = keyPair.getPublic();
        System.out.println("Public Key: " + pub);

        PrivateKey priv = keyPair.getPrivate();
        System.out.println("Private Key: " + priv);
    }

    public static void IntKeyPair() throws IOException, NoSuchAlgorithmException {
        return;
        /*KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();


        FileOutputStream fos = new FileOutputStream(path + "/public.key");
        fos.write(publicKey.getEncoded());
        fos.close();

        fos = new FileOutputStream(path + "/private.key");
        fos.write(privateKey.getEncoded());
        fos.close();

        dumpKeyPair(keyPair);*/
    }

    public static KeyPair LoadKeyPair(String path)
            throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        // Read Public Key.
        File publicKeyFile = new File(path + "/public.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        System.out.println(publicKey);
        // Read Private Key.
        File privateKeyFile = new File(path + "/private.key");
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        KeyPair pair = new KeyPair(publicKey, privateKey);
        System.out.println("Loaded Key Pair");
        dumpKeyPair(pair);

        return pair;
    }
}
