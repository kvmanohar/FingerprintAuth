package com.example.manoharkurapati.fingerprintauth;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.TextView;


/**
 * Created by manohar kurapati on 27/12/2016.
 */

public class FingerprintHelper extends FingerprintManager.AuthenticationCallback {

    private TextView errTv;
    private TextView otpTv;

    public FingerprintHelper(TextView errorTv, TextView otpTv){
        this.errTv = errorTv;
        this.otpTv = otpTv;
    }


    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        errTv.setText("Authentication Error.");
        otpTv.setText("");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        errTv.setText("Authentication Successful");
        otpTv.setText("1429");
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        errTv.setText("Authentication Failed");
        otpTv.setText("");
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
