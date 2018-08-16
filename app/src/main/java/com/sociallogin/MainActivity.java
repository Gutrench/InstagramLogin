package com.sociallogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sociallogin.instagram.InstagramLoginActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionListener();
    }

    private void setActionListener() {
        findViewById(R.id.instagram_login_btn).
                setOnClickListener(view -> startActivity(
                        new Intent(MainActivity.this,
                                InstagramLoginActivity.class)));
    }
}
