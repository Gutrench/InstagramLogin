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
    private Context context;
    private Dialog dialog;
    private ProgressDialog progressBar;
    private InstagramLoginListener instagramLoginListener;
    static final String REDIRECT_URI = "";
    static final String CLIENT_SECRET = "";
    static final String CLIENT_ID = "";
    static final String TOKEN_URL = String.format("https://api.instagram.com/oauth/access_token?client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code", CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
    private static final String AUTH_URL = "https://instagram.com/oauth/authorize/?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";

    private InstagramLoginManager(Context context) {
        this.context = context;
        setUpProgressBar();
    }

    private void setUpProgressBar() {
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(context.getString(R.string.text_loading));
    }

    public static InstagramLoginManager getInstance(Context context) {
        return new InstagramLoginManager(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public InstagramLoginManager login() {
        dialog = new Dialog(context);
        WebView webView = new WebView(context);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        MyWebViewClient myWebViewClient = new MyWebViewClient();
        myWebViewClient.setListener(this);
        webView.setWebViewClient(myWebViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AUTH_URL);
        dialog.setContentView(webView);
        return this;
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

    public void addLoginListener(InstagramLoginListener instagramLoginListener) {
        this.instagramLoginListener = instagramLoginListener;
    }
}