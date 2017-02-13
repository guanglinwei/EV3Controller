package com.example.ev3controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.controllibrary.HelloWorld;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HelloWorld hw = new HelloWorld();
        hw.sayHi();
    }
}
