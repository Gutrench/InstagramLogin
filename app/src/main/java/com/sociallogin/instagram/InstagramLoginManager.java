package com.sociallogin.instagram;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.webkit.WebView;

import com.sociallogin.R;

/**
 * Created by RISHABH on 8/8/18.
 */
public class InstagramLoginManager implements WebViewClientListener {
    private Dialog dialog;
    private ProgressDialog progressBar;
    private String redirectUri;
    private String clientSecret;
    private String clientId;
    private Context context;
    private String tokenUrl;
    private String authUrl;
    private InstagramLoginListener instagramLoginListener;

    private InstagramLoginManager(Builder builder) {
        this.context = builder.context;
        this.clientId = builder.clientId;
        this.redirectUri = builder.redirectUri;
        this.clientSecret = builder.clientSecret;
        this.instagramLoginListener = builder.instagramLoginListener;
        tokenUrl = String.format("https://api.instagram.com/oauth/access_token?client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code", clientId, clientSecret, redirectUri);
        authUrl = String.format("https://instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=code&display=touch", clientId, redirectUri);
        setUpProgressBar();
    }

    private void setUpProgressBar() {
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(context.getString(R.string.text_loading));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void login() {
        dialog = new Dialog(context);
        WebView webView = new WebView(context);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        MyWebViewClient myWebViewClient = new MyWebViewClient(clientId, clientSecret, redirectUri, tokenUrl);
        myWebViewClient.setListener(this);
        webView.setWebViewClient(myWebViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authUrl);
        dialog.setContentView(webView);
    }


    @Override
    public void hideProgress() {
        progressBar.dismiss();
        dialog.dismiss();
    }

    @Override
    public void onLoginSuccess(InstagramModel instagramModel) {
        instagramLoginListener.success(instagramModel);
    }

    @Override
    public void onLoginError(String message) {
        instagramLoginListener.error(message);
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
        private String redirectUri = "";
        private String clientSecret = "";
        private String clientId = "";
        private Context context;
        private InstagramLoginListener instagramLoginListener;

        public Builder with(final Context context) {
            this.context = context;
            return this;
        }

        public Builder setRedirectUri(final String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder setClientSecret(final String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder setClientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setListener(final InstagramLoginListener listener) {
            this.instagramLoginListener = listener;
            return this;
        }

        public InstagramLoginManager create() {
            return new InstagramLoginManager(this);
        }
    }
}