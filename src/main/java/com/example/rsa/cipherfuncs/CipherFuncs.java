package com.example.rsa.cipherfuncs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CipherFuncs {
    public String encryptMessage(String message, File publicKeyFile) throws IOException, GeneralSecurityException, Exception{
        PublicKey publicKey = loadPublicKey(publicKeyFile.getPath());
        byte[] encryptedBytes = encrypt(message.getBytes(), publicKey);
        return Base64.getEncoder().encodeToString(encryptedBytes);

    }

    public String decryptMessage(String message, File privateKeyFile) throws IOException, GeneralSecurityException, Exception{
        PrivateKey privateKey = loadPrivateKey(privateKeyFile.getPath());
        byte[] encryptedBytes = Base64.getDecoder().decode(message);
        byte[] decryptedBytes = decrypt(encryptedBytes, privateKey);
        return new String(decryptedBytes);
    }

    public void encryptDocument(File document, File openKey) throws IOException, GeneralSecurityException, Exception{
        PublicKey publicKey = loadPublicKey(openKey.getPath());
        byte[] documentBytes = Files.readAllBytes(document.toPath());
        byte[] encryptedBytes = encrypt(documentBytes, publicKey);
        String encryptedFileName = document.getName() + ".sde";
        Path encryptedFilePath = document.toPath().resolveSibling(encryptedFileName);
        Files.write(encryptedFilePath, encryptedBytes, StandardOpenOption.CREATE);
    }

    public void decryptDocument(File document, File secretKey) throws IOException, GeneralSecurityException, Exception{
        PrivateKey privateKey = loadPrivateKey(secretKey.getPath());
        byte[] encryptedBytes = Files.readAllBytes(document.toPath());
        byte[] decryptedBytes = decrypt(encryptedBytes, privateKey);
        String decryptedFileName = document.getName().replace(".sde", "");
        Path decryptedFilePath = document.toPath().resolveSibling(decryptedFileName);
        Files.write(decryptedFilePath, decryptedBytes, StandardOpenOption.CREATE);
    }

    public void signDocument(File document, File privateKey) throws Exception {
        byte[] documentBytes = Files.readAllBytes(document.toPath());
        PrivateKey closeKey = loadPrivateKey(privateKey.getPath());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(closeKey);
        signature.update(documentBytes);
        byte[] signatureBytes = signature.sign();
        String signatureFileName = document.getName() + ".sig";
        Path signatureFilePath = document.toPath().resolveSibling(signatureFileName);
        Files.write(signatureFilePath, signatureBytes, StandardOpenOption.CREATE);
    }

    public boolean verifyDocument(File document, File signFile, File publicKey) throws Exception{
        byte[] documentBytes = Files.readAllBytes(document.toPath());
        byte[] signatureBytes = Files.readAllBytes(signFile.toPath());
        PublicKey openKey = loadPublicKey(publicKey.getPath());
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(openKey);
        signature.update(documentBytes);
        return signature.verify(signatureBytes);
    }

    public static byte[] encrypt(byte[] plainText, PublicKey publicKey) {
        BigInteger n = ((java.security.interfaces.RSAPublicKey) publicKey).getModulus();
        BigInteger e = ((java.security.interfaces.RSAPublicKey) publicKey).getPublicExponent();
        BigInteger plainTextValue = new BigInteger(plainText);
        BigInteger encryptedValue = plainTextValue.modPow(e, n);
        return encryptedValue.toByteArray();
    }
    public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey) {
        BigInteger n = ((java.security.interfaces.RSAPrivateKey) privateKey).getModulus();
        BigInteger d = ((java.security.interfaces.RSAPrivateKey) privateKey).getPrivateExponent();
        BigInteger encryptedValue = new BigInteger(encryptedBytes);
        BigInteger decryptedValue = encryptedValue.modPow(d, n);
        byte[] decryptedBytes = decryptedValue.toByteArray();
        return decryptedBytes;
    }

    public static PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Path.of(publicKeyPath));
        String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8);
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Path.of(privateKeyPath));
        String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8);
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    public static void savePublicKey(PublicKey publicKey, String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(getPEMFormattedKey(publicKey.getEncoded(), "PUBLIC KEY"));
        }
    }

    public static void savePrivateKey(PrivateKey privateKey, String fileName) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(getPEMFormattedKey(privateKey.getEncoded(), "PRIVATE KEY"));
        }
    }

    public static byte[] getPEMFormattedKey(byte[] keyBytes, String keyType) {
        String formattedKey = "-----BEGIN " + keyType + "-----\n";
        formattedKey += Base64.getEncoder().encodeToString(keyBytes);
        formattedKey += "\n-----END " + keyType + "-----\n";
        return formattedKey.getBytes();
    }
}
