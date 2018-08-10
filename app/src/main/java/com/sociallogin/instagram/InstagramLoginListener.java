package com.sociallogin.instagram;

/**
 * Created by RISHABH on 10/8/18.
 */
public interface InstagramLoginListener {

    void success(InstagramModel instagramModel);

    void error(String message);
}
