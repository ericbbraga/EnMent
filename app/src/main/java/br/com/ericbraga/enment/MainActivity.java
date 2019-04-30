package br.com.ericbraga.enment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import br.com.ericbraga.enment.environmnet.transfer.FirebaseTransferFiles;
import br.com.ericbraga.enment.environmnet.transfer.UploadContract;

public class MainActivity extends AppCompatActivity {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                uploadFile();
            }
        }

    }

    private void uploadFile() {
        UploadContract contract = new FirebaseTransferFiles(sFirebaseBucket);
        File file = new File(Environment.getExternalStorageDirectory(), "alex_one_button.jpeg");

        contract.upload(file, "owner", new UploadContract.UploadCallback() {
            @Override
            public void onSuccess(Uri result) {
                Log.i("MainActivity", "onSuccess: Upload Success:" + result.toString());
            }

            @Override
            public void onError(String message) {
                Log.i("MainActivity", "On Error Occurred:" + message);
            }
        });
    }
}
