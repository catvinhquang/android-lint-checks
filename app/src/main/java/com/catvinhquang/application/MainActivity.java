package com.catvinhquang.application;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Size;

import com.catvinhquang.androidlintannotations.LargerThanZero;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String testCommentDetector = "Hello World!";

        testSizeAnnotationDetector(100);

        testLargerThanZeroAnnotationDetector(-100);

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    void testSizeAnnotationDetector(@Size int number) {
        System.out.println(number);
    }

    void testLargerThanZeroAnnotationDetector(@LargerThanZero int number) {
        System.out.println(number);
    }

}