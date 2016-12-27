package com.example.manoharkurapati.fingerprintauth;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

//    STEPS TO FOLLOW FOR THE FINGERPRINT AUTHENTICATION:

//    1. Request the permission to use the Fingerprint sensor in the Manifest file.
//    2. Verify that the lock screen is secure, or in other words, it is protected by PIN, password or pattern
//    3. Verify that at least one fingerprint is registered on the phone
//    4. Get access to Android keystore to store the key used to encrypt/decrypt an object
//    5. Generate an encryption key
//    6. Generate the Cipher
//    7. Start the authentication process
//    8. Implement a callback class to handle authentication events

    private static final String KEY_NAME="MS_Key";
    private TextView message;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;



    /**
        2. Verify that the lock screen is secure
        3. Verify that at lease one fingerprint is registered on the phone.
    */
    private boolean checkFinger(){

        // get KeyGuard manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //Get Fingerprint Manager
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        try{

            //Check the phone is secure with lock screen
            if (!keyguardManager.isKeyguardSecure()){
                message.setText("Secure lock screen not enabled.");
                return false;
            }

            //check if Fingerprint sensor is present
            if (!fingerprintManager.isHardwareDetected()){
                message.setText("Fingerprint Authentication not supported.");
                return false;
            }

            //Check if any fingerprint is enrolled in the phone
            if (!fingerprintManager.hasEnrolledFingerprints()){
                message.setText("No fingerprint configured");
                return false;
            }
        }
        catch (SecurityException se){
            se.printStackTrace();
        }

        return true;
    }

    /**
     *  4. Get access to Android keystore to store the key used to encrypt/decrypt an object
     *  5. Generate an encryption key
     */
    private void generateKey() throws FingerprintException {

        try{

            //Get the reference to the key store
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Key Generator to generate the key
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        }
        catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException e)
        {
            e.printStackTrace();
            throw new FingerprintException(e);
        }

    }


    /**
     * 6. Generate the Cipher
     *
     */
     private Cipher generateCipher() throws FingerprintException{

         try {
             Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                     + KeyProperties.BLOCK_MODE_CBC + "/"
                     + KeyProperties.ENCRYPTION_PADDING_PKCS7);

             SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
             cipher.init(Cipher.ENCRYPT_MODE,key);

             return cipher;

         }
         catch (NoSuchAlgorithmException
                 | NoSuchPaddingException
                 | InvalidKeyException
                 | UnrecoverableKeyException
                 | KeyStoreException e) {
             e.printStackTrace();
             throw new FingerprintException(e);
         }
     }

    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView) findViewById(R.id.fingerStatus);
        Button btn =  (Button) findViewById(R.id.authBtn);

        final FingerprintHelper fph = new FingerprintHelper(message);

        if (!checkFinger()){
            btn.setEnabled(false);
        }
        else {
            // We are ready to setup the cipher and the key
            try{
                generateKey();
                Cipher cipher = generateCipher();
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
            }
            catch (FingerprintException e){
                btn.setEnabled(false);
            }
        }


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                message.setText("Swipe your finger.");
                fph.doAuthentication(fingerprintManager,cryptoObject);
            }
        });

    }
}
