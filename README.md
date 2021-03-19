# WebDebugger

为了让非android开发人员更友好地在调试期间调试app，如果你有一下情节的困扰，那么此库非常适合你。
* 测试人员测试你的app出现bug了，需要截图、录像保存证据时
* 与后端联调时，需要查看本次请求的具体信息（如请求参数）
* UI在一旁指手画脚，叫你左移一点右移一点看效果
* 后端有多套开发环境，需要动态切换环境的需求
* 本地SQLite数据查看、修改（从 [Android-Debug-Database](https://github.com/amitshekhariitbhu/Android-Debug-Database) 中搬来）
* Mock请求数据
* 动态执行Java代码

## 安装

[![](https://jitpack.io/v/CiyLei/WebDebugger.svg)](https://jitpack.io/#CiyLei/WebDebugger)

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
debugImplementation 'com.github.CiyLei.WebDebugger:library:tag'
releaseImplementation 'com.github.CiyLei.WebDebugger:library-no-op:tag'
```
> tag替换成如1.3.2x（androidx版本库后面带x），support版本库不在维护，support版本库最后版本为1.3.2

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

    * 需要网络监听生效，要在你的 OkHttpClient 中添加此 `WebDebuggerInterceptor` 拦截器 和 `WebDebuggerNetEventListener` 事件监听
    * 需要切换环境生效，要调用一下方法 `WebDebugger.injectionRetrofit(retrofit, map, ApiServer::class.java)`
        * Retrofit: retrofit 对象
        * environment: 环境预配置（key为环境名，value为环境具体地址）
        * service: Retrofit 的接口访问类
    * 支持设备中心，将所有的设备注册到一个设备中心，方便查找，具体查看 [DeviceCenter](https://github.com/CiyLei/DeviceCenter) 项目

## 使用

浏览地址：手机ip:8080
> 端口由上面配置的 HTTP_PORT 决定，默认是 8080

查看 [api](https://github.com/CiyLei/WebDebugger/blob/master/Api.md)

## 效果

<img src="/img/screenshot2.png">

## 混淆

```proguard
-dontwarn com.dj.app.webdebugger.**
-keep class com.dj.app.webdebugger.** { *; }
-keep class com.amitshekhar.** { *; }
-keep class com.tencent.mars.** {
  public protected private *;
}
-keep class org.antlr.runtime.** { *; }
-keep class org.objectweb.asm.** { *; }
-keep class com.googlecode.** { *; }
-keep class com.android.dx.** { *; }
-keep class junit.** { *; }
```

## License
```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```