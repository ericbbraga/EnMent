package br.com.ericbraga.enment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final String sFirebaseBucket = "gs://enment-ericbraga.appspot.com";
    private Observable<String> mUploadFile;
    private List<Observer<String>> mObservers;
    private View mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mObservers = new ArrayList<>();

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initObservers();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                uploadFile();
            }
        }*/

       createMirrorObserver();
       createInvertObserver();
    }

    private void createMirrorObserver() {

        mObservers.add(new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i("MainActivity", "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }


        });
    }

    private void createInvertObserver() {
        mObservers.add(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                StringBuilder reverseString = new StringBuilder(s).reverse();
                Log.i("MainActivity", "onNext: " + reverseString.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }


        });
    }

    private void initObservers() {

        mUploadFile = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("Something");
            }
        });

        for (Observer<String> observer : mObservers) {
            mUploadFile.subscribe(observer);
        }
    }
}
