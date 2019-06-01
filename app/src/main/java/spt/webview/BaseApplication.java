package spt.webview;

import android.app.Application;

import spt.webview.tools.WebViewTools;
import spt.webview.utils.ProcessUtils;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/5
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = ProcessUtils.getAppProcess(this);
        System.out.println("processName = " + processName);
        if ("spt.webview:web".equals(processName)) {
            // 初始化WebViewTools
            WebViewTools.getInstance().initialize(this);
        }
    }

}
