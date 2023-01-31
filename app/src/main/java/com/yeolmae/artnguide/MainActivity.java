package com.yeolmae.artnguide;

//import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yeolmae.R;
import com.yeolmae.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    private static final int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    private ProgressBar progressBar;
    AlertDialog alertIsp;
    SwipeRefreshLayout refreshLayout;
    ActivityMainBinding binding;
    public static ApiManager apiManager;

    private static final String TAG = "MainActivity";
    private static final String COMMON_AGENTS = "Mozilla/5.0 AppleWebKit/535.19 Chrome/56.0.0 Mobile Safari/535.19";

    //    @SuppressLint( "SetJavaScriptEnabled")
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        alertIsp =
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("알림")
                        .setMessage(
                                "모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n 설치를 눌러 진행 해 주십시요. \n 취소를 누르면 결제가 취소 됩니다."
                        )
                        .setPositiveButton(
                                "설치",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        webView.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andowb.jsp");
                                        finish();
                                    }
                                }
                        )
                        .setNegativeButton(
                                "취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast
                                                .makeText(
                                                        MainActivity.this,
                                                        "(-1)결제를 취소 하셨습니다.",
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show();
                                        finish();
                                    }
                                }
                        )
                        .create();

        //progressbar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webSettings.setMixedContentMode(webSettings.MIXED_CONTENT_NEVER_ALLOW); //Insecurity 페이지 허용 이니시스 관련셋팅
        webSettings.setUserAgentString(COMMON_AGENTS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new TestWebViewClient(this));
        webView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onCreateWindow(
                            final WebView view,
                            boolean dialog,
                            boolean userGesture,
                            Message resultMsg
                    ) {
                        WebView newWebView = new WebView(MainActivity.this);
                        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                        transport.setWebView(newWebView);
                        resultMsg.sendToTarget();

                        newWebView.setWebViewClient(
                                new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(
                                            WebView view,
                                            String url
                                    ) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        browserIntent.setData(Uri.parse(url));
                                        startActivity(browserIntent);
                                        return true;
                                    }
                                }
                        );

                        return true;
                    }
                }
        );

        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webView.reload();
                        // 새로고침 완료
                        refreshLayout.setRefreshing(false);
                    }
                }
        );
        refreshLayout
                .getViewTreeObserver()
                .addOnScrollChangedListener(
                        new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
                                if (webView.getScrollY() == 0) {
                                    refreshLayout.setEnabled(true);
                                } else {
                                    refreshLayout.setEnabled(false);
                                }
                            }
                        }
                );

        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("url");

        if (url != null) {
            webView.loadUrl(url);
        }

        getInfo(cookieManager);
    }


    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent intent
    ) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 100) {
                if (uploadMessage == null) return;
                uploadMessage.onReceiveValue(
                        WebChromeClient.FileChooserParams.parseResult(resultCode, intent)
                );
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;

            Uri result = intent == null || resultCode != MainActivity.RESULT_OK
                    ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else Toast
                .makeText(
                        getApplicationContext(),
                        "Failed to Upload Image",
                        Toast.LENGTH_LONG
                )
                .show();
    }

    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        webView.stopLoading();
        ViewGroup webParent = (ViewGroup) webView.getParent();
        if (webParent != null) {
            webParent.removeView(webView);
        }
        webView.destroy();
    }

    private void getInfo(CookieManager cookieManager) {
//        String HOME = "https://new.devartnguide.com/";
        String HOME = "https://artnguide.co.kr";

        try {
            WebkitCookieManagerProxy webkitCookieManagerProxy = new WebkitCookieManagerProxy();
            String cookies = webkitCookieManagerProxy.get(HOME);
            String[] valueCookies = cookies.split("=");
//            System.out.print("valueCookies " + valueCookies);
//            System.out.print("valueCookies " + valueCookies[1]);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
//                String msg = getString(R.string.fcm_token, token);
                            Log.d(TAG, "token:" + token);
                            if (token != "") {
//                                injectJS(webView,token);
                                FireBaseModel fireBaseModel = new FireBaseModel(valueCookies[1], token);
                                apiManager = ApiManager.getInstance();
                                apiManager.saveFirebase(fireBaseModel, new Callback<FireBaseModel>() {

                                    @Override
                                    public void onResponse(Call<FireBaseModel> call, Response<FireBaseModel> response) {
                                        if (response.isSuccessful() && response != null) {
                                            Log.i("Responsive", String.valueOf(response));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FireBaseModel> call, Throwable t) {
                                        Log.i("Responsive", String.valueOf(t));
                                    }
                                });
                            }
                        }
                    });
        } catch (Exception exception) {
            Log.e("exception", String.valueOf(exception));
        }
    }
}
