package spt.webview;

import android.app.Application;
import android.util.Log;

import spt.webview.tools.InterceptTools;
import spt.webview.tools.WebViewTools;
import spt.webview.utils.ProcessUtils;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/5
 */
public class BaseApplication extends Application {

    // TAG
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = ProcessUtils.getAppProcess(this);
        Log.i(TAG, "processName = " + processName);
        if ("spt.webview:web".equals(processName)) {
            // 初始化WebViewTools
            WebViewTools.getInstance().initialize(this);
            InterceptTools.getInstance().initialize();
        } else {
            // 启动WebView进程
            WebViewTools.startProcess(this);
        }
    }

}
