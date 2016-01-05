package it.jaschke.alexandria;

import android.content.Intent;
import android.util.Log;

import com.journeyapps.barcodescanner.CaptureActivity;

/**
 * Created by Tiggi on 1/5/2016.
 */
public class BarcodeScanner extends CaptureActivity {
    private static final String TAG = BarcodeScanner.class.getSimpleName();

    public BarcodeScanner() {
        Log.d(TAG, "BarcodeScanner()");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d( TAG, "onActivityResult()");
    }

}
