package spt.webview.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/15
 */
public class ProcessUtils {

    /**
     * 获取进程名称
     *
     * @param context 参数为当前上下文对象
     * @return 返回当前进程名称
     */
    public static String getAppProcess(Context context) {
        int currentPid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : list) {
            if (currentPid == info.pid) {
                return info.processName;
            }
        }
        return null;
    }
}
