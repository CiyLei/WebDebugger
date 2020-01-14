# WebDebugger

在Web中查看Android的各种信息

## 安装

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" } // 添加jitpack
    }
}
```
```groovy
debugImplementation 'com.ciy:WebDebugger:1.0.0'
releaseImplementation 'com.ciy:WebDebugger-no-op:1.0.0'
```

## 接入

1. 配置

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

2. 开启

    在 `Application` 中开启
    ```kotlin
    class MyApplication : Application() {
        override fun onCreate() {
            WebDebugger.install(this)
        }
    }
    ```
    
3. 辅助框架监听权限

    在你的 `BaseActivity` 中重新以下方法
    ```kotlin
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        WebDebugger.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WebDebugger.onActivityResult(requestCode, resultCode, data)
    }
    ```

4. 其余设置

    * 需要网络监听生效，要在你的 OkHttpClient 中添加此 `WebDebuggerInterceptor` 拦截器
    * 需要切换环境生效，要调用一下方法 `WebDebugger.injectionRetrofit(retrofit, map, ApiServer::class.java)`
        * Retrofit: retrofit 对象
        * environment: 环境预配置（key为环境名，value为环境具体地址）
        * service: Retrofit 的接口访问类
    * 支持设备中心，将所有的设备注册到一个设备中心，方便查找，具体查看 [DeviceCenter](https://github.com/CiyLei/DeviceCenter) 项目

## 使用

查看 [api](https://github.com/CiyLei/WebDebugger/blob/master/Api.md)

### 设备信息模块

实时监控FPS、内存情况，查看手机信息

![设备信息模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/%E8%AE%BE%E5%A4%87%E4%BF%A1%E6%81%AF.png)

### logcat模块

实时查看 logcat

![logcat模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/logcat.png)

### 截屏/录屏模块

提供一键截屏、一键开始录屏、结束录屏，实时反馈

![截屏/录屏模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/%E6%88%AA%E5%B1%8F%E5%BD%95%E5%B1%8F.png)

### 网络日志模块

实时查看网络请求日志和查看历史请求日志

![网络日志模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/%E7%BD%91%E7%BB%9C%E6%97%A5%E5%BF%97.png)

### 切换环境模块

一键切换 Retrofit 的 baseUrl

![切换环境模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/%E5%88%87%E6%8D%A2%E7%8E%AF%E5%A2%83.png)

### Api清单模块

查看 Retrofit 的接口类信息

![Api清单模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/api%E6%B8%85%E5%8D%95.png)

### Adb支持模块

![Adb支持模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/adb.png)

### 查看/修改数据库模块

从 [Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database) 中搬来

![查看/修改数据库模块](https://raw.githubusercontent.com/CiyLei/WebDebugger/master/img/db.png)

## 混淆

```proguard
-dontwarn com.dj.app.webdebugger.**
-keep class com.dj.app.webdebugger.** { *; }
-keep class com.amitshekhar.** { *; }
-keep class com.tencent.mars.** {
  public protected private *;
}
```