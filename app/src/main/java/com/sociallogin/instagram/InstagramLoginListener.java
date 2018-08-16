package com.sociallogin.instagram;

public interface InstagramLoginListener {

    void success(InstagramModel instagramModel);

    void error(String message);
}
