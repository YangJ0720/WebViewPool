# WebViewPool
Android客户端自定义WebView缓存池，用于抵消启动浏览器内核的耗时

# Android客户端WebView加载优化

### 一、使用WebView缓存
由于WebView初始化需要消耗一定的时间，并且不同设备之间该耗时时间略有差异。这里我使用

### 二、图片延迟加载


### 三、页面资源预加载
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
