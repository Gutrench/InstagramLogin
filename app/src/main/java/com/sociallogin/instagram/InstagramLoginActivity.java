package com.sociallogin.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sociallogin.R;
import com.sociallogin.Utils;

import java.io.IOException;

public class InstagramLoginActivity extends AppCompatActivity {
    private InstagramLoginListener instagramLoginListener = new InstagramLoginListener() {
        @Override
        public void success(InstagramModel instagramModel) {
            //Handle user data here.
        }

        @Override
        public void error(String message) {
            //Handle error here.
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);
        setActionListener();
    }

    private void setActionListener() {
        findViewById(R.id.instagram_btn).setOnClickListener(view -> {
            try {
                InstagramLoginManager instagramLoginManager = new InstagramLoginManager
                        .Builder().with(InstagramLoginActivity.this)
                        .setClientId(Utils.getProperty("CLIENT_ID", InstagramLoginActivity.this))
                        .setClientSecret(Utils.getProperty("CLIENT_SECRET", InstagramLoginActivity.this))
                        .setRedirectUri(Utils.getProperty("REDIRECT_URI", InstagramLoginActivity.this))
                        .setListener(instagramLoginListener).create();
                instagramLoginManager.login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}