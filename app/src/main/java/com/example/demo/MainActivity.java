package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.demo.layout.LinearActivity;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView)findViewById(R.id.textView)).setText(stringFromJNI());
    }

    public native String stringFromJNI();

    public void goLinearLayout(View view) {
        startActivity(new Intent(this, LinearActivity.class));
    }
}