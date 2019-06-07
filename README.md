# WebViewPool
Android客户端自定义WebView缓存池，用于抵消启动浏览器内核的耗时

# Android客户端WebView加载优化

### 一、使用WebView缓存
由于WebView初始化需要消耗一定的时间，并且不同设备之间该耗时时间略有差异。这里我使用Profiler来抓取WebView初始化耗时时间，以下抓取结果均来自真机：
Yulong Coolpad 5370
<img src="https://github.com/YangJ0720/WebViewPool/blob/master/jpg/Yulong Coolpad 5370.png" width="800" height="300"/>
Google Nexus 6
<img src="https://github.com/YangJ0720/WebViewPool/blob/master/jpg/Google Nexus 6.png" width="800" height="300"/>
Motorola XT1789-05
<img src="https://github.com/YangJ0720/WebViewPool/blob/master/jpg/Motorola XT1789-05.png" width="800" height="300"/>
从图中可以看到WebView初始化存在一定的耗时时间，而且在配置较低的设备上耗时较长，为了优化这部分耗时，我们可以定义全局WebView对象并提前初始化，如果需要使用WebView加载页面直接使用初始化好的WebView对象就可以了。
在本工程中，我自定义一个访问优先的WebView缓存池，默认大小为3。每一次访问WebView缓冲池都会从末尾节点向前遍历，如果找到对应url的WebView对象，就直接返回，并将该节点移动到末尾；如果没有找到，则返回第一个节点的WebView对象，并将该节点移动到末尾。

### 二、页面资源预加载
服务端提供相关接口用于增量同步页面资源包，Android客户端将资源包下载到SDCard并解压。每当页面开始加载资源的时候，首先检查本地是否存在该资源文件，如果存在直接返回本地资源文件而不是去网络中加载。我们需要借助Native方法shouldInterceptRequest来实现该功能，示例代码如下：
```
@Override
public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    Log.i(TAG, "shouldInterceptRequest: " + url);
    return super.shouldInterceptRequest(view, url);
}

@Override
public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
    Log.d(TAG, "shouldInterceptRequest: " + request);
    return super.shouldInterceptRequest(view, request);
}
```
