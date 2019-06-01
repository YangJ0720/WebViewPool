package spt.webview.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 功能描述
 *
 * @author YangJ
 * @since 2019/5/15
 */
public class WebStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive: " + intent);
    }
}
