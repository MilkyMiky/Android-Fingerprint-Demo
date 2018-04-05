package com.example.mike.myapplication;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    private Cipher cipher;
    private KeyStore keyStore;
    private final static String KEY_NAME="KEY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);

        KeyguardManager keymanger = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this,"DETECTED",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NOT DETECTED", Toast.LENGTH_LONG).show();
        }

        if(fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this,"Has enrolled",Toast.LENGTH_LONG).show();
        }

        if(keymanger.isKeyguardSecure()){
                Toast.makeText(this,"Secured",Toast.LENGTH_LONG).show();
        }

        getKey();

        if( cipherInit()){

                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                FingerprintHandler handler = new FingerprintHandler(this);
                handler.startAuth(fingerprintManager,cryptoObject);

            }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"
            +KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            } catch (IOException e1) {
                return false;
//                e1.printStackTrace();


            } catch (NoSuchAlgorithmException e1) {
                return false;
//                e1.printStackTrace();
            } catch (CertificateException e1) {
                return false;
//                e1.printStackTrace();
            } catch (UnrecoverableKeyException e1) {
                return false;
//                e1.printStackTrace();
            } catch (KeyStoreException e1) {
                return false;
//                e1.printStackTrace();
            } catch (InvalidKeyException e1) {
                return false;
            }

    }

    private void getKey() {

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT
                    | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            );
            keyGenerator.generateKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

}

