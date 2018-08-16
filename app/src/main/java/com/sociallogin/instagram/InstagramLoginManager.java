package com.sociallogin.instagram;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.sociallogin.R;

public class InstagramLoginManager implements WebViewClientListener {
    private final String TOKEN_URL;
    private final String AUTH_URL;
    private final String REDIRECT_URI;
    private final String CLIENT_SECRET;
    private final String CLIENT_ID;
    private Dialog dialog;
    private Context context;
    private ProgressDialog progressBar;
    private InstagramLoginListener instagramLoginListener;

    private InstagramLoginManager(Builder builder) {
        this.context = builder.context;
        this.CLIENT_ID = builder.clientId;
        this.CLIENT_SECRET = builder.clientSecret;
        this.REDIRECT_URI = builder.redirectUri;
        this.instagramLoginListener = builder.instagramLoginListener;
        TOKEN_URL = String.format("https://api.instagram.com/oauth/access_token?client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code", CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
        AUTH_URL = String.format("https://instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=code&display=touch", CLIENT_ID, REDIRECT_URI);
    }

    private void setUpProgressBar() {
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(context.getString(R.string.text_loading));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void login() {
        setUpProgressBar();
        dialog = new Dialog(context);
        WebView webView = new WebView(context);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        InstagramWebViewClient instagramWebViewClient = new InstagramWebViewClient.Builder()
                .setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).setTokenUrl(TOKEN_URL)
                .setRedirectUri(REDIRECT_URI).setWebViewClientListener(this)
                .setLoginListener(instagramLoginListener).build();
        webView.setWebViewClient(instagramWebViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AUTH_URL);
        dialog.setContentView(webView);
    }


    @Override
    public void hideProgress() {
        progressBar.dismiss();
        dialog.dismiss();
    }

    @Override
    public void onPageFinished() {
        progressBar.dismiss();
        dialog.show();
    }

    @Override
    public void showProgress() {
        progressBar.show();
    }

    static class Builder {
        private String redirectUri;
        private String clientSecret;
        private String clientId;
        private Context context;
        private InstagramLoginListener instagramLoginListener;

        public Builder with(@NonNull final Context context) {
            this.context = context;
            return this;
        }

        public Builder setRedirectUri(@NonNull final String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder setClientSecret(@NonNull final String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setClientId(@NonNull final String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setListener(@NonNull final InstagramLoginListener listener) {
            this.instagramLoginListener = listener;
            return this;
        }

        public InstagramLoginManager build() {
            InstagramLoginManager instagramLoginManager = new InstagramLoginManager(this);
            if (TextUtils.isEmpty(instagramLoginManager.REDIRECT_URI)) {
                throw new IllegalStateException(
                        "Please set redirect URI");
            } else if (TextUtils.isEmpty(instagramLoginManager.CLIENT_ID)) {
                throw new IllegalStateException(
                        "Please set client id");
            } else if (TextUtils.isEmpty(instagramLoginManager.CLIENT_SECRET)) {
                throw new IllegalStateException(
                        "Please set client secret");
            } else if (instagramLoginManager.context == null) {
                throw new IllegalStateException(
                        "Context can not be null");
            }
            return instagramLoginManager;
        }
    }
}