package spt.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import spt.webview.tools.InterceptTools;

/**
 * Created by YangJ on 2019/3/24.
 */
public class WrapWebView extends WebView {

    /**
     * WebView默认url
     */
    public static final String DEF_WEB_VIEW_URL = "about:blank";

    private Context mContext;
    private View mErrorView;

    // TAG
    private static final String TAG = "WrapWebView";

    public WrapWebView(Context context) {
        super(context);
        initialize(context);
    }

    public WrapWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public WrapWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
    }

    private void showErrorView(boolean show) {
        if (mErrorView == null) {
            mErrorView = View.inflate(mContext, R.layout.include_error, null);
            mErrorView.setLayoutParams(getLayoutParams());
            mErrorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    reload();
                }
            });
        }
        if (show) {
            addView(mErrorView);
        } else {
            removeView(mErrorView);
        }
    }

    @Override
    public void goBack() {
        int currentIndex = copyBackForwardList().getCurrentIndex();
        if (currentIndex <= 1) {
            // 停止加载页面
            stopLoading();
        }
        super.goBack();
    }

    /**
     * 判断页面是否可以后退
     *
     * @return 返回true表示可以后退，返回false反之
     */
    protected boolean isGoBack() {
        int currentIndex = copyBackForwardList().getCurrentIndex();
        boolean isGoBack = false;
        if (currentIndex >= 2) {
            isGoBack = true;
        }
        goBack();
        return isGoBack;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new CustomWebViewClient(client));
    }

    class CustomWebViewClient extends WebViewClient {

        private WebViewClient mWebViewClient;

        public CustomWebViewClient(WebViewClient client) {
            mWebViewClient = client;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mWebViewClient != null) {
                mWebViewClient.onPageStarted(view, url, favicon);
            }
            showErrorView(false);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (mWebViewClient != null) {
                mWebViewClient.onLoadResource(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mWebViewClient != null) {
                mWebViewClient.shouldOverrideUrlLoading(view, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (mWebViewClient != null) {
                mWebViewClient.shouldOverrideUrlLoading(view, request);
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mWebViewClient != null) {
                mWebViewClient.shouldInterceptRequest(view, url);
            }
            Log.i(TAG, "shouldInterceptRequest: url = " + url);
            WebResourceResponse response = super.shouldInterceptRequest(view, url);
            InterceptTools tools = InterceptTools.getInstance();
            if (tools.isExist(url)) {
                response = tools.getWebResourceResponse(mContext.getAssets(), url);
            }
            Log.i(TAG, "shouldInterceptRequest: response = " + response);
            return response;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (mWebViewClient != null) {
                mWebViewClient.shouldInterceptRequest(view, request);
            }
            Log.d(TAG, "shouldInterceptRequest: request = " + request);
            Uri uri = request.getUrl();
            String url = uri.getPath();
            WebResourceResponse response = super.shouldInterceptRequest(view, request);
            InterceptTools tools = InterceptTools.getInstance();
            if (tools.isExist(url)) {
                response = tools.getWebResourceResponse(mContext.getAssets(), url);
            }
            Log.d(TAG, "shouldInterceptRequest: response = " + response);
            return response;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mWebViewClient != null) {
                    mWebViewClient.onReceivedError(view, request, error);
                }
            }
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (request.isForMainFrame() || request.getUrl().toString().equals(getUrl())) {
                    showErrorView(true);
                }
            }
            Log.e(TAG, "onReceivedError: " + error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e(TAG, "onReceivedError: " + error.getErrorCode());
                Log.e(TAG, "onReceivedError: " + error.getDescription());
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (mWebViewClient != null) {
                mWebViewClient.onReceivedSslError(view, handler, error);
            }
            super.onReceivedSslError(view, handler, error);
            Log.e(TAG, "onReceivedSslError: " + error);
            Log.e(TAG, "onReceivedSslError: " + error.getPrimaryError());
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                errorResponse.getStatusCode();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mWebViewClient != null) {
                mWebViewClient.onPageFinished(view, url);
            }
            Log.i(TAG, "onPageFinished");
        }
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(new CustomWebChromeClient(client));
    }

    private class CustomWebChromeClient extends WebChromeClient {

        private WebChromeClient mWebChromeClient;

        public CustomWebChromeClient(WebChromeClient client) {
            mWebChromeClient = client;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (mWebChromeClient != null) {
                mWebChromeClient.onProgressChanged(view, newProgress);
            }
        }
    }
}
