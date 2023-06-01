package com.example.rsa.controller;

import com.example.rsa.cipherfuncs.CipherFuncs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    Window currentWindow;

    CipherFuncs cipherFuncs;

    PrivateKey privateKey;

    PublicKey publicKey;

    @FXML
    private Label cryptStatus;

    @FXML
    private Label saveKeyStatus;

    @FXML
    private Label decryptStatus;

    @FXML
    private TextArea decryptedText;

    @FXML
    private TextField docFilePath;

    @FXML
    private TextField documentToCryptPath;

    @FXML
    private TextField documentToDecryptPath;

    @FXML
    private TextField documentToSignPath;

    @FXML
    private TextField encryptedBase64Text;

    @FXML
    private TextField encryptedTextField;

    @FXML
    private TextField openKeyPath;

    @FXML
    private TextField privateKeyForSignPath;

    @FXML
    private TextField privateKeyPath;

    @FXML
    private TextField pubKeyPath;

    @FXML
    private TextField publicKeyPath;

    @FXML
    private TextField secretKeyPath;

    @FXML
    private TextField sigFilePath;

    @FXML
    private Label singStatus;

    @FXML
    private TextArea textToEncryptField;

    @FXML
    private Label verifyStatus;

    @FXML
    private Label clipboardStatus;

    @FXML
    private TextArea privateKeyTextArea;

    @FXML
    private TextArea publicKeyTextArea;


    @FXML
    void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(currentWindow);

        if(file == null){
            return;
        }

        Button btn = (Button) event.getSource();

        switch (btn.getId()){
            case "openKeyFileBtn" -> openKeyPath.setText(file.getAbsolutePath());
            case "secretKeyBtn" -> secretKeyPath.setText(file.getAbsolutePath());
            case "cryptDocumentBtn" -> documentToCryptPath.setText(file.getAbsolutePath());
            case "decryptDocumentBtn" -> documentToDecryptPath.setText(file.getAbsolutePath());
            case "publicKeyFileBtn" -> publicKeyPath.setText(file.getAbsolutePath());
            case "privateKeyBtn" -> privateKeyPath.setText(file.getAbsolutePath());
            case "documentToSignBtn" -> documentToSignPath.setText(file.getAbsolutePath());
            case "privateKeyForSignBtn" -> privateKeyForSignPath.setText(file.getAbsolutePath());
            case "sigFileBtn" -> sigFilePath.setText(file.getAbsolutePath());
            case "docToVerifyBtn" -> docFilePath.setText(file.getAbsolutePath());
            case "publicKeyForVerifyBtn" -> pubKeyPath.setText(file.getAbsolutePath());
        }
    }
    @FXML
    void generateKeys() throws NoSuchAlgorithmException{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        byte[] formattedPublicKey = cipherFuncs.getPEMFormattedKey(publicKey.getEncoded(), "PUBLIC KEY");
        byte[] formattedPrivateKey = cipherFuncs.getPEMFormattedKey(privateKey.getEncoded(), "PRIVATE KEY");
        publicKeyTextArea.setText(new String(formattedPublicKey));
        privateKeyTextArea.setText(new String(formattedPrivateKey));
    }

    @FXML
    void savePrivateKey() throws  IOException{
        cipherFuncs.savePrivateKey(privateKey, "private.pem");
        saveKeyStatus.setText("Закрытый ключ успешно сохранен");
    }

    @FXML
    void savePublicKey() throws IOException {
        cipherFuncs.savePublicKey(publicKey, "public.pem");
        saveKeyStatus.setText("Открытый ключ успешно сохранен");
    }

    @FXML
    void encryptText() throws Exception {
        if(publicKeyPath.getText().isEmpty()){
            encryptedTextField.setText("Отсутствует публичный ключ для шифрования");
            return;
        }

        if(textToEncryptField.getText().isEmpty()){
            encryptedTextField.setText("Отсутствует текст для шифрования");
            return;
        }

        File publicKey = new File(publicKeyPath.getText());

        String message = textToEncryptField.getText();

        String encryptedMessage = cipherFuncs.encryptMessage(message, publicKey);

        encryptedTextField.setText(encryptedMessage);
    }

    @FXML
    void decryptMessage() throws Exception {
        if(privateKeyPath.getText().isEmpty()){
            decryptedText.setText("Отсутствует приватный ключ для дешифровки");
            return;
        }

        if(encryptedBase64Text.getText().isEmpty()){
            decryptedText.setText("Отсутствует Base64 для дешифровки");
            return;
        }

        File privateKey = new File(privateKeyPath.getText());

        String base64Message = encryptedBase64Text.getText();

        String decryptedMessage = cipherFuncs.decryptMessage(base64Message, privateKey);

        decryptedText.setText(decryptedMessage);
    }

    @FXML
    void cryptDocument() throws Exception {
        if(openKeyPath.getText().isEmpty()){
            cryptStatus.setText("Отсутствует файл открытого ключа!");
            return;
        }

        if(documentToCryptPath.getText().isEmpty()){
            cryptStatus.setText("Выберите файл для шифрования");
            return;
        }

        File documentToEncrypt = new File(documentToCryptPath.getText());
        File openKey = new File(openKeyPath.getText());

        cipherFuncs.encryptDocument(documentToEncrypt, openKey);

        cryptStatus.setText("Документ успешно зашифрован");
    }

    @FXML
    void decryptDocument() throws Exception {
        if(secretKeyPath.getText().isEmpty()){
            decryptStatus.setText("Отсутствует файл закрытого ключа!");
            return;
        }

        if(documentToDecryptPath.getText().isEmpty()){
            decryptStatus.setText("Выберите файл для шифрования");
            return;
        }

        File documentToDecrypt = new File(documentToDecryptPath.getText());
        File privateKey = new File(secretKeyPath.getText());

        cipherFuncs.decryptDocument(documentToDecrypt, privateKey);

        decryptStatus.setText("Документ успешно расшифрован");
    }

    @FXML
    public void sign() throws Exception {
        if(documentToSignPath.getText().isEmpty()){
            singStatus.setText("Отсутствует документ для подписи");
            return;
        }

        if(privateKeyForSignPath.getText().isEmpty()){
            singStatus.setText("Отсутствует приватный ключ для подписи");
            return;
        }

        File documentForSign = new File(documentToSignPath.getText());

        File privateKeyFile = new File(privateKeyForSignPath.getText());

        cipherFuncs.signDocument(documentForSign, privateKeyFile);

        singStatus.setText("Документ успешно подписан");
    }

    @FXML
    public void verify() throws Exception {
        if(pubKeyPath.getText().isEmpty()){
            verifyStatus.setText("Отсутствует публичный ключ");
            return;
        }

        if(sigFilePath.getText().isEmpty()){
            verifyStatus.setText("Отсутствует файл цифровой подписи");
            return;
        }

        if(docFilePath.getText().isEmpty()){
            verifyStatus.setText("Отсутствует документ для проверки");
            return;
        }

        File sigFile = new File(sigFilePath.getText());

        File docFile = new File(docFilePath.getText());

        File pubKeyFile = new File(pubKeyPath.getText());

        if(cipherFuncs.verifyDocument(docFile, sigFile, pubKeyFile)){
            verifyStatus.setText("Подлинность подтверждена");
        } else {
            verifyStatus.setText("Подпись невалидна");
        }
    }

    @FXML
    public void copyToClipboard() {
        if (encryptedTextField.getText().isEmpty()){
            clipboardStatus.setText("Вывод пуст");
            return;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();

        ClipboardContent clipboardContent = new ClipboardContent();

        clipboardContent.put(DataFormat.PLAIN_TEXT, encryptedTextField.getText());

        clipboard.setContent(clipboardContent);

        clipboardStatus.setText("Сообщение скопировано в буфер обмена");
    }

    public MainController(Window window){
        this.currentWindow = window;
    }

    public MainController(){}


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cipherFuncs = new CipherFuncs();
    }


}