package com.sociallogin.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sociallogin.R;

import java.io.IOException;

public class InstagramLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);
        setActionListener();
    }

    private void setActionListener() {
        try {
            Log.e("secret: ", InstagramUtils.getProperty("CLIENT_ID", this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        findViewById(R.id.instagram_btn).setOnClickListener(view ->
                InstagramLoginManager.getInstance(InstagramLoginActivity.this)
                        .login().addLoginListener(new InstagramLoginListener() {
                    @Override
                    public void success(InstagramModel instagramModel) {
                        Log.e("Full Name ", instagramModel.getUser().getFull_name());
                    }

                    @Override
                    public void error(String message) {

                    }
                }));
    }
}