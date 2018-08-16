package com.sociallogin.instagram;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class InstagramWebViewClient extends WebViewClient {
    private final String REDIRECT_URI;
    private final String CLIENT_SECRET;
    private final String CLIENT_ID;
    private final String TOKEN_URL;
    private WebViewClientListener webViewClientListener;
    private InstagramLoginListener loginListener;
    private String code;

    InstagramWebViewClient(Builder builder) {
        this.TOKEN_URL = builder.tokenUrl;
        this.CLIENT_ID = builder.clientId;
        this.REDIRECT_URI = builder.redirectUri;
        this.CLIENT_SECRET = builder.clientSecret;
        this.loginListener = builder.loginListener;
        this.webViewClientListener = builder.webViewClientListener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        webViewClientListener.showProgress();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (request.getUrl().toString().startsWith(REDIRECT_URI)) {
            handleUrl(request.getUrl().toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(REDIRECT_URI)) {
            handleUrl(url);
            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        webViewClientListener.onPageFinished();
    }

    @SuppressWarnings("unused")
    private void handleUrl(String url) {
        if (url.contains("code")) {
            webViewClientListener.showProgress();
            String temp[] = url.split("=");
            code = temp[1];

            DisposableObserver<InstagramModel> disposableObserver = Observable.fromCallable(() -> {
                URL url1 = new URL(TOKEN_URL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url1.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write(String.format("client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s"
                        , CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, code));
                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                return new Gson().fromJson(jsonObject.toString(), InstagramModel.class);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<InstagramModel>() {

                @Override
                public void onNext(InstagramModel instagramModel) {
                    if (webViewClientListener != null) {
                        webViewClientListener.hideProgress();
                    }

                    if (loginListener != null) {
                        loginListener.success(instagramModel);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (webViewClientListener != null) {
                        webViewClientListener.hideProgress();
                    }

                    if (loginListener != null) {
                        loginListener.error(e.getLocalizedMessage());
                    }
                }

                @Override
                public void onComplete() {

                }
            });
        } else if (url.contains("error")) {
            String temp[] = url.split("=");
            if (loginListener != null) {
                loginListener.error(temp[temp.length - 1]);
            }
        }
    }

    private static String streamToString(InputStream is) throws IOException {
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

            } finally {
                is.close();
            }
            str = sb.toString();
        }
        return str;
    }

    static class Builder {
        private String redirectUri;
        private String clientSecret;
        private String clientId;
        private String tokenUrl;
        private InstagramLoginListener loginListener;
        private WebViewClientListener webViewClientListener;

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

        public Builder setTokenUrl(@NonNull final String tokenUrl) {
            this.tokenUrl = tokenUrl;
            return this;
        }

        public Builder setLoginListener(@NonNull final InstagramLoginListener listener) {
            this.loginListener = listener;
            return this;
        }

        public Builder setWebViewClientListener(@NonNull final WebViewClientListener listener) {
            this.webViewClientListener = listener;
            return this;
        }

        public InstagramWebViewClient build() {
            InstagramWebViewClient instagramWebViewClient = new InstagramWebViewClient(this);
            if (TextUtils.isEmpty(instagramWebViewClient.REDIRECT_URI)) {
                throw new IllegalStateException(
                        "Please set redirect URI");
            } else if (TextUtils.isEmpty(instagramWebViewClient.CLIENT_ID)) {
                throw new IllegalStateException(
                        "Please set client id");
            } else if (TextUtils.isEmpty(instagramWebViewClient.CLIENT_SECRET)) {
                throw new IllegalStateException(
                        "Please set client secret");
            } else if (TextUtils.isEmpty(instagramWebViewClient.TOKEN_URL)) {
                throw new IllegalStateException(
                        "Token URL can not be null");
            }
            return instagramWebViewClient;
        }
    }
}