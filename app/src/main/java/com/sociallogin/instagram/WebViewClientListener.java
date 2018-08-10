package com.sociallogin.instagram;

/**
 * Created by RISHABH on 8/8/18.
 */
public interface WebViewClientListener {
    void hideProgress();

    void onLoginSuccess(InstagramModel instagramModel);

    void onLoginError(String message);

    void onPageFinished();

    void showProgress();
}
