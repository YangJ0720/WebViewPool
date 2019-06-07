package spt.webview.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/6/1
 */
public class InterceptTools {

    private static final String ENCODING = "UTF-8";
    private static final int STATUS_CODE = 200;
    private static final String REASON_PHRASE = "ok";

    private static final String FILE_FORMAT_JS = ".js";
    private static final String MIME_TYPE_JS = "text/javascript";
    private static final String FILE_FORMAT_CSS = ".css";
    private static final String MIME_TYPE_CSS = "text/css";
    private static final String FILE_FORMAT_PNG = ".png";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final String FILE_FORMAT_JPG = ".jpg";
    private static final String MIME_TYPE_JPG = "image/jepg";

    private static final class SingletonHolder {
        private static final InterceptTools INSTANCE = new InterceptTools();
    }

    public static InterceptTools getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ArrayMap<String, String> mArrayMap;

    private InterceptTools() {
        mArrayMap = new ArrayMap<>();
    }

    public void initialize() {
        // 模拟服务端同步页面资源到客户端，并保存到SDCard
        mArrayMap.put("/custom.js", "Atlanta/assets/js/custom.js");
        mArrayMap.put("/webview.js", "Atlanta/assets/js/webview.js");
        mArrayMap.put("/logo.png", "Atlanta/assets/images/logo.png");
        mArrayMap.put("/arrows.png", "Atlanta/assets/images/arrows.png");
        mArrayMap.put("/person_1.png", "Atlanta/assets/images/person_1.png");
        mArrayMap.put("/person_2.png", "Atlanta/assets/images/person_2.png");
        mArrayMap.put("/person_3.png", "Atlanta/assets/images/person_3.png");
        mArrayMap.put("/bg_header.jpg", "Atlanta/assets/images/bg_header.jpg");
    }

    public boolean isExist(String url) {
        return !TextUtils.isEmpty(getLocalResource(url));
    }

    public String getLocalResource(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        int lastIndex = url.lastIndexOf("/");
        int length = url.length();
        String fileName = url.substring(lastIndex, length);
        if (mArrayMap.containsKey(fileName)) {
            return mArrayMap.get(fileName);
        }
        return null;
    }

    public WebResourceResponse getWebResourceResponse(Context context, String url) {
        return getWebResourceResponse(context.getAssets(), url);
    }

    public WebResourceResponse getWebResourceResponse(AssetManager manager, String url) {
        String filePath = getLocalResource(url);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        InputStream stream = null;
        try {
            stream = manager.open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String mimeType = checkMimeType(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ArrayMap<String, String> responseHeaders = new ArrayMap<>(2);
            responseHeaders.put("Access-Control-Allow-Origin", "*");
            responseHeaders.put("Access-Control-Allow-Headers", "Content-Type");
            return new WebResourceResponse(mimeType, ENCODING, STATUS_CODE, REASON_PHRASE, responseHeaders, stream);
        } else {
            return new WebResourceResponse(mimeType, ENCODING, stream);
        }
    }

    private String checkMimeType(String filePath) {
        if (filePath.endsWith(FILE_FORMAT_JS)) {
            return MIME_TYPE_JS;
        } else if (filePath.endsWith(FILE_FORMAT_PNG)) {
            return MIME_TYPE_PNG;
        } else if (filePath.endsWith(FILE_FORMAT_JPG)) {
            return MIME_TYPE_JPG;
        }
        return MIME_TYPE_CSS;
    }

}
