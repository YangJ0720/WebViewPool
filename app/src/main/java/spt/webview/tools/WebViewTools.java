package spt.webview.tools;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.webkit.WebView;

import spt.webview.WrapWebView;
import spt.webview.pool.WebViewPool;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/5
 */
public class WebViewTools {

    private static final String ACTION_START_WEB = "spt.webview.web.start.action";
    private static final String ACTION_STOP_WEB = "spt.webview.web.stop.action";

    // WebView缓存池
    private WebViewPool<String, WrapWebView> mPool;

    private WebViewTools() {

    }

    private static final class SingletonHolder {
        private static final WebViewTools INSTANCE = new WebViewTools();
    }

    public static WebViewTools getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 启动WebView进程
     *
     * @param context 参数为当前上下文对象
     */
    public static void startProcess(Context context) {
        Intent intent = new Intent(ACTION_START_WEB);
        context.sendBroadcast(intent);
    }

    /**
     * 结束WebView进程
     *
     * @param context 参数为当前上下文对象
     */
    public static void stopProcess(Context context) {
        Intent intent = new Intent(ACTION_STOP_WEB);
        context.sendBroadcast(intent);
    }

    /**
     * 初始化WebView缓存池
     *
     * @param context 参数为当前上下文对象
     */
    public void initialize(Context context) {
        mPool = new WebViewPool<>(context);
    }

    /**
     * 根据要加载的页面url地址，从WebView缓存池中获取WebView对象，该方法遵循最近访问原则
     * <p>如果没有找到对应url地址的WebView对象，默认返回链表最前面的节点</p>
     *
     * @param url 参数为要加载的页面url地址
     * @return 根据最近访问原则返回一个WebView对象
     */
    public WrapWebView getWebView(String url) {
        return mPool.get(url);
    }

    /**
     * 将WebView对象从父容器中移除
     *
     * @param webView 参数为WebView对象
     */
    public void removeWebView(WebView webView) {
        webView.stopLoading();
        ViewGroup viewGroup = (ViewGroup) webView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(webView);
        }
        webView.removeAllViews();
        webView.setWebViewClient(null);
        webView.setWebChromeClient(null);
    }

    /**
     * 将WebView对象从父容器中移除
     *
     * @param viewGroup 参数为WebView所在的父容器
     * @param webView   参数为WebView对象
     */
    public void removeWebView(ViewGroup viewGroup, WebView webView) {
        viewGroup.removeView(webView);
        webView.stopLoading();
        webView.setWebViewClient(null);
        webView.setWebChromeClient(null);
    }
}
