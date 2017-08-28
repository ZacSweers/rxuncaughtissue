package com.example.rxuncaughtissue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.Thread.UncaughtExceptionHandler;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.test_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simulateError();
            }
        });
    }

    private void simulateError() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                Log.e("UNCAUGHT", "simulateError - Received error!");
                final UncaughtExceptionHandler saved = Thread.currentThread().getUncaughtExceptionHandler();
                Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        Thread.currentThread().setUncaughtExceptionHandler(saved);
                        Log.e("UNCAUGHT", "simulateError - Rethrowing");
                        throw (RuntimeException) throwable;
                    }
                });
                Log.e("UNCAUGHT", "simulateError - Initial throw");
                throw new RuntimeException(e);
            }
        });
        Observable.error(new RuntimeException("This gets buried"))
                .subscribe();
    }
}
