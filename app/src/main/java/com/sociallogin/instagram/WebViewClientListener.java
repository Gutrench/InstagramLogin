package com.sociallogin.instagram;

public interface WebViewClientListener {
    void hideProgress();

    void onPageFinished();

    void showProgress();
}
