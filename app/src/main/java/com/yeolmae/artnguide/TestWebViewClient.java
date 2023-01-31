package com.yeolmae.artnguide;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import androidx.annotation.RequiresApi;

import java.net.URISyntaxException;


public class TestWebViewClient extends WebViewClient {

//    public static final String INTENT_URI_START = "intent:";
    public static final String INTENT_FALLBACK_URL = "browser_fallback_url";
    public static final String URI_SCHEME_MARKET = "market://details?id=";
//    public static final String HOME = "https://new.devartnguide.com/";
    public static final String HOME = "https://artnguide.co.kr/";

    private Context context;
    private MainActivity mMain;

    public TestWebViewClient(Context context) {
        super();

        this.context = context;
        this.mMain = (MainActivity)context;

        this.mMain.webView.loadUrl(HOME);
    }

    public TestWebViewClient() {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        System.out.println("shouldOverrideUrlLoading request : " + request.getMethod() + " " + request.getUrl());
        String url = request.getUrl().toString();

        if(!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")){
            Intent intent;
            try{
                intent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
            }
            catch (URISyntaxException ex){
                return false;
            }

            try{
                this.mMain.startActivity(intent);
            }

            catch (ActivityNotFoundException e){
                if(url.startsWith("ispmobile://")){
                    this.mMain.showDialog(1005);
                    return false;
                }
                else if(url.startsWith("intent")){
                    try{
                        Intent tempIntent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                        String strParams = tempIntent.getDataString();

                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse(strParams));

                        this.mMain.startActivity(intent2);
                        return true;
                    }
                    catch (Exception e1) {

                        e1.printStackTrace();
                        Intent intent3 = null;

                        try{
                            intent3 = Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id="+intent3.getPackage()));
                            this.mMain.startActivity(marketIntent);
                        }
                        catch (Exception e2){
                            e2.printStackTrace();
                        }
                        return true;
                    }
                }
            }
        }
        else{
            view.loadUrl(url);
            return false;
        }
        return true;
    }

    private boolean doFallback(WebView view, Intent parsedIntent) {
        System.out.println("doFallback(WebView view, Intent parsedIntent)");
        if (parsedIntent == null) {
            return false;
        }

        String fallbackUrl = parsedIntent.getStringExtra(INTENT_FALLBACK_URL);
        if (fallbackUrl != null) {
            view.loadUrl(fallbackUrl);
            return true;
        }

        String packageName = parsedIntent.getPackage();
        if (packageName != null) {
            this.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_SCHEME_MARKET + packageName)));
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        System.out.println("onPageStarted url : " + url);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        System.out.println("onPageFinished url : " + url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        System.out.println("onLoadResource url : " + url);
        super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        System.out.println("onPageCommitVisible url : " + url);
        super.onPageCommitVisible(view, url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        System.out.println("shouldInterceptRequest request : " + request.getMethod() + " " + request.getUrl());
        return super.shouldInterceptRequest( this.mMain.webView, request);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        System.out.println("onReceivedError request : " + request.getMethod() + " " + request.getUrl());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("onReceivedError error : " + error.getErrorCode() + " " + error.getDescription());
        }
        super.onReceivedError(view, request, error);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest
            request, WebResourceResponse errorResponse) {
        System.out.println("onReceivedHttpError request : " + request.getMethod() + " " + request.getUrl());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            System.out.println("onReceivedHttpError error : " + errorResponse.getStatusCode());
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        System.out.println("onFormResubmission ");
        super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        System.out.println("onFormResubmission url : " + url);
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        System.out.println("onReceivedSslError");
        System.out.println("onReceivedSslError error : " + error.getUrl());

        super.onReceivedSslError(view, handler, error);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        System.out.println("onReceivedClientCertRequest request :" + request.getHost());
        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String
            host, String realm) {
        System.out.println("onReceivedHttpAuthRequest host : " + host);
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        System.out.println("shouldOverrideKeyEvent KeyCode : " + event.getKeyCode());
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        System.out.println("onUnhandledKeyEvent KeyCode : " + event.getKeyCode());
        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        System.out.println("onScaleChanged oldScale : " + oldScale + " | " + newScale);
        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        System.out.println("onReceivedLoginRequest");
        super.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        System.out.println("onRenderProcessGone");
        return super.onRenderProcessGone(view, detail);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request,
                                  int threatType, SafeBrowsingResponse callback) {
        System.out.println("onSafeBrowsingHit");
        super.onSafeBrowsingHit(view, request, threatType, callback);
    }
}