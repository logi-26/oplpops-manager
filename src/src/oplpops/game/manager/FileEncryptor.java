package oplpops.game.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class FileEncryptor {
    
    public FileEncryptor(){}
    
    // This encrypts a file from a list of strings
    public void EncryptData(List<String> originalData, String outputFilePath, String secretKeyFilePath){
        try { 
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
            DESKeySpec ks = (DESKeySpec) skf.getKeySpec(secretKey, spec);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(secretKeyFilePath))) {
                outputStream.writeObject(ks.getKey());
                Cipher cipher = Cipher.getInstance("DES/CFB8/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(outputFilePath), cipher);
                try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(cos))) {for (String line : originalData){printWriter.println(line);}}
                outputStream.writeObject(cipher.getIV());
            }
        } catch (NoSuchAlgorithmException | ClassNotFoundException | InvalidKeySpecException | NoSuchPaddingException | IOException | InvalidKeyException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
    }
    
    // This returns a list of decrypted strings from an encrypted file
    public List<String> DecryptData(String inputFilePath, String secretKeyFilePath){
        
        List<String> decryptedData = new ArrayList<>();
        
        try { 
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(secretKeyFilePath));
            DESKeySpec ks = new DESKeySpec((byte[]) objectInputStream.readObject());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = skf.generateSecret(ks);

            Cipher cipher = Cipher.getInstance("DES/CFB8/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec((byte[]) objectInputStream.readObject()));
            CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(inputFilePath), cipher);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cipherInputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {decryptedData.add(line);}

        } catch (NoSuchAlgorithmException | ClassNotFoundException | InvalidKeySpecException | NoSuchPaddingException | IOException | InvalidKeyException | InvalidAlgorithmParameterException ex) {PopsGameManager.displayErrorMessageDebug(ex.toString());}
        
        return decryptedData;
    }
}