package spt.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import spt.webview.tools.WebViewTools;

/**
 * @author YangJ
 */
public class BrowserActivity extends AppCompatActivity {

    private static final String ARG_PARAM_URL = "url";

    // Data
    private String mUrl;
    // View
    private ViewGroup mViewGroup;
    private WrapWebView mWebView;
    private ProgressBar mProgressBar;

    // TAG
    private static final String TAG = "BrowserActivity";

    public static void startBrowserActivity(Context context, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(ARG_PARAM_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initData();
        initView();
    }

    private void initData() {
        mUrl = getIntent().getStringExtra(ARG_PARAM_URL);
    }

    private void initView() {
        mViewGroup = findViewById(R.id.frameLayout);
        // 使用Chrome调试页面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView = WebViewTools.getInstance().getWebView(mUrl);
        //
        Log.i(TAG, "mWebView = " + mWebView);
        Toast.makeText(this, "mWebView = " + mWebView, Toast.LENGTH_LONG).show();
        //
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (mProgressBar.getVisibility() != View.VISIBLE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished: " + url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.loadUrl(mUrl);
        mViewGroup.addView(mWebView, 0);
        mProgressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        WebViewTools.getInstance().removeWebView(mWebView);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (mWebView.isGoBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
