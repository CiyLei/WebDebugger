# Api 说明

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