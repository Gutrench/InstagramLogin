package com.sociallogin.instagram;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
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


/**
 * Created by RISHABH on 8/8/18.
 */
public class MyWebViewClient extends WebViewClient {
    private String code;
    private WebViewClientListener listener;
    private String redirectUri;
    private String clientSecret;
    private String clientId;
    private String tokenUrl;

    MyWebViewClient(String clientId, String clientSecret, String redirectUri, String tokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        listener.showProgress();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (request.getUrl().toString().startsWith(redirectUri)) {
            handleUrl(request.getUrl().toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(redirectUri)) {
            handleUrl(url);
            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        listener.onPageFinished();
    }

    @SuppressWarnings("unused")
    private void handleUrl(String url) {
        if (url.contains("code")) {
            listener.showProgress();
            String temp[] = url.split("=");
            code = temp[1];

            DisposableObserver<InstagramModel> disposableObserver = Observable.fromCallable(() -> {
                URL url1 = new URL(tokenUrl);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url1.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + redirectUri +
                        "&code=" + code);

                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                return new Gson().fromJson(jsonObject.toString(), InstagramModel.class);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<InstagramModel>() {

                @Override
                public void onNext(InstagramModel instagramModel) {
                    if (listener != null) {
                        listener.onLoginSuccess(instagramModel);
                        listener.hideProgress();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (listener != null) {
                        listener.onLoginError(e.getLocalizedMessage());
                        listener.hideProgress();
                    }
                }

                @Override
                public void onComplete() {

                }
            });
        } else if (url.contains("error")) {
            String temp[] = url.split("=");
            listener.onLoginError(temp[temp.length - 1]);
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

    public void setListener(WebViewClientListener listener) {
        this.listener = listener;
    }
}