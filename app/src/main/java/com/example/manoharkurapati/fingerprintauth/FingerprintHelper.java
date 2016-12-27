package com.example.manoharkurapati.fingerprintauth;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.TextView;


/**
 * Created by manoharkurapati on 27/12/2016.
 */

public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private TextView tv;

    public FingerprintHelper(TextView tv){
        this.tv = tv;
    }


    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        tv.setText("Authentication Error.");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        tv.setText("Authentication Successful");
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        tv.setText("Authentication Failed");
    }


    public void doAuthentication(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal signal = new CancellationSignal();

        try{
            manager.authenticate(cryptoObject,signal,0,this,null);
        }
        catch (SecurityException se) {

        }
    }


}
