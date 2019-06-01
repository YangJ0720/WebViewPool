package spt.webview.pool.node;

import android.webkit.WebView;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/25
 */
public class WebViewNode<T extends WebView> {

    private WebViewNode mPrevious;
    private T mT;

    public WebViewNode(WebViewNode previous, T item) {
        this.mPrevious = previous;
        this.mT = item;
    }

    public boolean hasPrevious() {
        return mPrevious != null;
    }

    public int getSize() {
        int size = 0;
        if (hasPrevious()) {
            WebViewNode node = mPrevious;
            do {

                size++;
            }
            while (node.hasPrevious());
        }
        return size;
    }
}
