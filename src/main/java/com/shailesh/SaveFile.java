package com.shailesh;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SaveFile {
    enum Mode {
        DECRYPT_FILE,
        ENCRYPT_FILE,
        ENCRYPT_AND_CLEAN_DIARY
    }

    /* Init private key from secrete file */
    private static int privateKey;
    private static int mod;

    /* Public Key to encrypt the file */
    private static final int publicKey = 139;

    /* By default this file would be encrypted */
    private static final String fileToEncrypt = "src/main/resources/DailyDiary.txt";
    private static final String encryptedFileLocation = "src/main/java/com/shailesh/files";

    public static void main(String[] args) throws Exception {
        validateInput(args);

        System.out.println("Running with mode : " + args[0]);
        Mode mode = Mode.valueOf(args[0]);
        initPrivateKeys();
        switch (mode) {
            case ENCRYPT_FILE:
                EncryptFile();
                break;
            case DECRYPT_FILE:
                DecryptFile(args);
                break;
            case ENCRYPT_AND_CLEAN_DIARY:
                EncryptAndCleanDiary();
                break;
            default:
                break;
        }
    }

    private static void validateInput(String[] args) {
        System.out.println("Validation args : " + Arrays.toString(args));
        if (args.length < 1) {
            String msg = "Expect an argument specifying the mode of operation : " + Arrays.toString(Mode.values());
            System.out.println(msg);
            throw new RuntimeException(msg);
        }
        String mode = args[0];
        if(Mode.DECRYPT_FILE.name().equals(mode) ){
            if (args.length < 2) {
                String msg = "In DECRYPT mode expect absolute filename as second param";
                System.out.println(msg);
                throw new RuntimeException(msg);
            }
        }
    }

    private static void EncryptAndCleanDiary() throws Exception {
        EncryptFile();
        CleanDiary();
    }

    private static void CleanDiary() throws IOException {
        System.out.println("Cleaning file : " + fileToEncrypt);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileToEncrypt));
        bufferedWriter.write("");
        bufferedWriter.close();
    }


    private static void initPrivateKeys() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode key = objectMapper.readTree(new File("D:\\KeyPair\\key.json"));
        privateKey = key.get("privateKey").asInt();
        mod = key.get("mod").asInt();
    }

    private static void DecryptFile(String[] args) throws Exception {
        String fileToDecrypt = args[1];
        System.out.println("Decryting file : " + fileToDecrypt);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToDecrypt, StandardCharsets.UTF_8));
        int n;
        StringBuilder stringBuilder = new StringBuilder();
        while ((n = bufferedReader.read()) != -1) {
            int result = (n * privateKey) % mod;
            stringBuilder.append((char) result);
        }
        System.out.println("Below is the Decrypted Content - ");
        System.out.println(stringBuilder);
        bufferedReader.close();
    }

    private static void EncryptFile() throws Exception {
        String outFilePath = createDirectoryForFileIfMissing();
        System.out.println("Encrypting data to " + outFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFilePath, StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToEncrypt, StandardCharsets.UTF_8));

        int n;
        List<Integer> data = new ArrayList<>();
        while ((n = bufferedReader.read()) != -1) {
            int result = (n * publicKey) % mod;
            data.add(result);
        }
        char[] encData = new char[data.size()];
        for (int i = 0; i < data.size(); i++) {
            int d = data.get(i);
            encData[i] = (char) d;
        }
        System.out.println("Enc Data : " + Arrays.toString(encData));
        bufferedWriter.write(encData);

        bufferedReader.close();
        bufferedWriter.close();
    }

    private static String createDirectoryForFileIfMissing() throws IOException {
        Date date = new Date();
        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd.hh-mm-ssa");
        String date_full = sdfFull.format(date);

        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd");
        String date_year = sdfYear.format(date);
        String date_month = sdfMonth.format(date);
        String date_date = sdfDate.format(date);

        if (!Files.isDirectory(Paths.get(encryptedFileLocation + "/" + date_year))) {
            Files.createDirectory(Paths.get(encryptedFileLocation + "/" + date_year));
        }
        if (!Files.isDirectory(Paths.get(encryptedFileLocation + "/" + date_year + "/" + date_month))) {
            Files.createDirectory(Paths.get(encryptedFileLocation + "/" + date_year + "/" + date_month));
        }
        if (!Files.isDirectory(Paths.get(encryptedFileLocation + "/" + date_year + "/" + date_month + "/" + date_date))) {
            Files.createDirectory(Paths.get(encryptedFileLocation + "/" + date_year + "/" + date_month + "/" + date_date));
        }

        return encryptedFileLocation + "/" + date_year + "/" + date_month + "/" + date_date + "/" + "EncryptedDiary" + date_full + ".txt";
    }


}
