package spt.webview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import spt.webview.tools.WebViewTools;

/**
 * @author YangJ
 */
public class MainActivity extends AppCompatActivity {

    // TAG
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        // 结束WebView进程
        WebViewTools.stopProcess(this);
        super.onDestroy();
    }

    private void initData() {

    }

    private void initView() {
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, getData()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = (String) parent.getAdapter().getItem(position);
                // 跳转到WebView
                BrowserActivity.startBrowserActivity(MainActivity.this, url);
            }
        });
    }

    private List<String> getData() {
        ArrayList<String> list = new ArrayList<>();
        list.add("file:///android_asset/Atlanta/index.html");
        list.add("https://weibo.com/");
        list.add("https://github.com/");
        list.add("https://china.nba.com/");
        list.add("https://www.baidu.com/");
        list.add("https://map.baidu.com/");
        list.add("https://www.youku.com/");
        list.add("https://music.163.com/");
        list.add("https://www.hao123.com");
        list.add("https://www.taobao.com");
        list.add("https://www.bilibili.com/");
        list.add("https://flutterchina.club/");
        list.add("http://192.168.1.3:8080/Atlanta/index.html");
        return list;
    }

}
