package com.example.rsa.cipherfuncs;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
public class CipherFuncs {
    public String encryptMessage(String message, File publicKeyFile) throws IOException, GeneralSecurityException{
        return null;
    }

    public String decryptMessage(String message, File privateKeyFile) throws IOException, GeneralSecurityException{
        return null;
    }

    public void encryptDocument(File document, File openKey) throws IOException, GeneralSecurityException{

    }

    public void decryptDocument(File document, File secretKey) throws IOException, GeneralSecurityException{

    }

    public void signDocument(File document, File privateKey) throws IOException, GeneralSecurityException{

    }

    public boolean verifyDocument(File document, File signFile, File publicKey) throws IOException, GeneralSecurityException{
        return false;
    }
}
