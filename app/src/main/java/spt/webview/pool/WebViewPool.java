package spt.webview.pool;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import spt.webview.WrapWebView;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/25
 */
public class WebViewPool<K extends String, V extends WebView> {

    // WebView缓存池大小
    private static final int DEF_WEB_VIEW_CACHE_COUNT = 3;

    // 链表长度
    private int mSize;
    // 链表末尾节点
    private Node<K, V> mLastNode;

    // TAG
    private static final String TAG = "WebViewPool";

    public WebViewPool(Context context) {
        int i = 0;
        while (i < DEF_WEB_VIEW_CACHE_COUNT) {
            add((K) WrapWebView.DEF_WEB_VIEW_URL, initWebView(context));
            i++;
        }
    }

    /**
     * 初始化WebView
     *
     * @param context 参数为当前上下文对象
     * @return 返回一个WebView对象
     */
    private V initWebView(Context context) {
        WebView webView = new WrapWebView(context);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true); // 可任意比例缩放
        // 设置支持js
        webSettings.setJavaScriptEnabled(true);
        // 设置渲染优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }
        webSettings.setAllowContentAccess(true);
        webSettings.setDisplayZoomControls(true);
        //
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        // 开启数据库形式存储
        webSettings.setDatabaseEnabled(true);
        // 开启DOM形式存储
        webSettings.setDomStorageEnabled(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 特别注意：5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        webView.loadUrl(WrapWebView.DEF_WEB_VIEW_URL);
        return (V) webView;
    }

    /**
     * 将WebView添加到链表
     *
     * @param key   参数为页面url地址
     * @param value 参数为WebView对象
     */
    private void add(K key, V value) {
        Node newNode = new Node<>(key, value, mLastNode, null);
        if (mLastNode != null) {
            mLastNode.next = newNode;
        }
        mLastNode = newNode;
        mSize++;
    }

    /**
     * 移除末尾节点
     *
     * @return 返回true表示移除成功，返回false表示失败
     */
    private boolean removeLast() {
        if (mLastNode == null) {
            return false;
        }
        Node prev = mLastNode.prev;
        mLastNode.prev = null;
        mLastNode = null;
        if (prev != null) {
            prev.next = null;
            mLastNode = prev;
        }
        mSize--;
        return true;
    }

    /**
     * 移除所有节点
     *
     * @return 返回true表示移除成功，返回false表示失败
     */
    private boolean removeAll() {
        while (removeLast()) {

        }
        return true;
    }

    /**
     * 获取链表长度
     *
     * @return 返回int类型的链表长度
     */
    public int getSize() {
        return mSize;
    }

    /**
     * 从末尾节点往前查找目标节点，如果没有找到就返回最前面的节点
     *
     * @param key 参数为key
     * @return 返回一个目标节点
     */
    public V get(K key) {
        if (mLastNode == null) {
            return null;
        }
        Node currentNode = mLastNode;
        while (true) {
            Node prev = currentNode.prev;
            if (prev == null) {
                // 将最前面的节点移动到末尾
                changeNodeToLast(currentNode);
                currentNode.key = key;
                return (V) currentNode.value;
            } else if (currentNode.key.equals(key)) {
                changeNodeToLast(currentNode);
                return (V) currentNode.value;
            } else {
                currentNode = prev;
            }
        }
    }

    /**
     * 将节点移动到末尾
     *
     * @param targetNode 参数为需要移动到末尾的节点
     */
    private void changeNodeToLast(Node targetNode) {
        // 如果是末尾节点，那么不需要移动
        Node next = targetNode.next;
        if (next == null) {
            return;
        }
        Node prev = targetNode.prev;
        // 如果目标节点的前驱节点为null则意味着目标节点在最前面
        if (prev == null) { // 目标节点在最前面
            // 设置目标节点的后继节点prev为null
            next.prev = null;
        } else { // 目标节点处于中间位置
            prev.next = next;
            next.prev = prev;
        }
        targetNode.next = null;
        targetNode.prev = mLastNode;
        mLastNode.next = targetNode;
        mLastNode = targetNode;
    }

    /**
     * 从末尾向前遍历所有节点
     */
    public void show() {
        if (mLastNode == null) {
            Log.w(TAG, "WebViewPool is null");
            return;
        }
        Node currentNode = mLastNode;
        while (true) {
            Log.i(TAG, "currentNode.value = " + currentNode.value);
            Node prev = currentNode.prev;
            if (prev == null) {
                break;
            } else {
                currentNode = prev;
            }
        }
    }

    private static class Node<K, V> {
        // 在该DEMO中key表示url地址
        private K key;
        // 在该DEMO中value表示WebView对象
        private V value;
        // 前驱节点
        private Node<K, V> prev;
        // 后继节点
        private Node<K, V> next;

        public Node(K key, V value, Node<K, V> prev, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
