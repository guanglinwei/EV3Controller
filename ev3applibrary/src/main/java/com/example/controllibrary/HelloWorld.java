package com.example.controllibrary;

/**
 * Created by guang on 1/22/2017.
 */

public class HelloWorld {
    private String name="gw";
    public void sayHi() {
        System.out.println("Hello "+name);

    }
    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
//        System.out.println("Hello, World");
        HelloWorld hw = new HelloWorld();
        hw.sayHi();
    }
}
