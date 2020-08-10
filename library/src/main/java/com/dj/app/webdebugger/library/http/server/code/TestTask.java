package com.dj.app.webdebugger.library.http.server.code;

import android.widget.Toast;

import java.util.Random;

/**
 * Create by ChenLei on 2020/8/7
 * Describe:
 */
public class TestTask extends TaskExecutor {

    @Override
    public void execute() {
        out.println("你好");
        out.println(123);
        System.out.println(new Random().nextInt());
        Toast.makeText(getContext(), "123", Toast.LENGTH_SHORT).show();
    }
}
