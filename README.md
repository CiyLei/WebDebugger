# WebDebugger

在Web中查看Android的各种信息

## 开始

项目一共有三个服务器：Http服务器、WebSocket服务器、静态资源服务器，所以需要指定三个端口

如果不设置默认是以下端口
```GROOVY
android {
    defaultConfig {
        resValue("string", "HTTP_PORT", "8080") //Http服务器端口
        resValue("string", "WEB_SOCKET_PORT", "8081") //WebSocket服务器端口
        resValue("string", "RESOURCE_PORT", "8082") //静态资源服务器端口
    }
}
```


## 模块
* 设备信息模块
* logcat模块
* 网络日志模块
* 截屏/录屏模块
* 切换环境模块
* 查看/修改数据库模块（未实现）

### 设备信息模块

| 地址 | 请求方式 | 所需参数 | 说明 |
|---|---|---|---|
| /device/info | GET | 无 | 查看设备信息的接口 |
| /device | webSocket | 无 | 实时接收内存、FPS情况 |

### logcat模块

| 地址 | 请求方式 | 所需参数 | 说明 |
|---|---|---|---|
| /logcat | webSocket | 无 | 实时接收logcat |
    
### 网络日志模块

| 地址 | 请求方式 | 所需参数 | 说明 |
|---|---|---|---|
| /logcat/net | webSocket | 无 | 实时接收网络请求数据 |
    
### 截屏/录屏模块

| 地址 | 请求方式 | 所需参数 | 说明 |
|---|---|---|---|
| /media/screenCapture | GET | 无 | 截屏 |
| /media/startScreenRecording | GET | 无 | 开始录屏 |
| /media/stopScreenRecording | GET | 无 | 结束录屏 |
| /media/list | GET | 无 | 查看所有的媒体缓存 |
| /media/clean | GET | 无 | 清除所有的媒体缓存 |
| /media/add | webSocket | 无 | 实时接收添加的媒体文件信息 |
    
### 切换环境模块

| 地址 | 请求方式 | 所需参数 | 说明 |
|---|---|---|---|
| /retrofit/info | GET | 无 | 查看当前环境 |
| /retrofit/edit | POST | newUrl | 设置新的地址 |
    
### 查看/修改数据库模块（未实现）

有时间从[Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database)中挖过来
