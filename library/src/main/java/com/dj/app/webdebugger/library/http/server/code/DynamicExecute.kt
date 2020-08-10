package com.dj.app.webdebugger.library.http.server.code

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.utils.DynamicCompilerUtil
import dalvik.system.PathClassLoader

/**
 * Create by ChenLei on 2020/8/10
 * Describe: 动态执行代码
 */
class DynamicExecute private constructor(
    private val mContext: Context,
    private val mImportCode: String,
    private val mCode: String
) {

    companion object {
        // 生成的包名
        private val GENERATE_PACKAGE = TaskExecutor::class.java.`package`.name

        // 生成的类名的前缀
        private val GENERATE_CLASS_PREFIX = "Task_"

        /**
         * 只能通过此方法创建实例
         */
        fun newInstance(
            context: Context,
            importCode: String,
            code: String
        ): DynamicExecute = DynamicExecute(context, importCode, code)
    }

    /**
     * 当前时间戳，将作为生成Class的类名的后缀，保证每次生成的Class都不同
     */
    private val mTime = System.currentTimeMillis()

    /**
     * 类名
     */
    private val mClassName = "$GENERATE_CLASS_PREFIX$mTime"

    /**
     * 输出的内容
     */
    var outContent: String = ""

    /**
     * 开始执行
     */
    fun execute(runOnMainThread: Boolean = false): Boolean {
        // 生成Java代码
        var javaSource = generateJavaSource(mImportCode, mCode)
        // 替换System.out为TaskExecutor中的out
        javaSource = javaSource.replace("System.out", "out")
        // 编译Java代码
        val classData =
            DynamicCompilerUtil.compile(javaSource, mContext.classLoader) ?: return false
        // class数据写入jar文件
        val jarPath =
            DynamicCompilerUtil.class2Jar(mContext, GENERATE_PACKAGE, mClassName, classData)
                ?: return false
        // jar文件转dex文件
        val dexPath = DynamicCompilerUtil.jar2Dex(jarPath) ?: return false
        // 加载dex文件
        val cl = PathClassLoader(dexPath, mContext.classLoader)
        // 完整的类名
        val completeClassName = "$GENERATE_PACKAGE.$mClassName"
        // 反射调用执行代码
        val taskExecutor =
            cl.loadClass(completeClassName).newInstance() as? TaskExecutor ?: return false
        run(taskExecutor, runOnMainThread)
        return true
    }

    /**
     * 生成Java代码
     * android里面用不了javax的一些类，无法使用javapoet框架，那就字符串替换吧
     * @param
     * @param import 需要导入包名
     * @param code 执行的代码
     * @return 生成的Java源代码
     */
    private fun generateJavaSource(import: String, code: String): String {
        return "package $GENERATE_PACKAGE;\n" +
                "\n" +
                import +
                "\n" +
                "public class $mClassName extends TaskExecutor {\n" +
                "\n" +
                "    @Override\n" +
                "    public void execute() {\n" +
                "        $code\n" +
                "    }\n" +
                "}\n"
    }

    /**
     * 执行任务
     * @param task 执行的任务
     * @param runOnMainThread 是否运行在主线程
     */
    private fun run(task: TaskExecutor, runOnMainThread: Boolean) {
        if (runOnMainThread) {
            val o = Object()
            Handler(Looper.getMainLooper()).post {
                try {
                    task.run()
                } catch (e: Exception) {
                    // 添加异常信息
                    e.printStackTrace(task.out)
                }
                synchronized(o) {
                    o.notifyAll()
                }
            }
            try {
                // 等待主线程执行完毕（最多等等3秒）
                synchronized(o) {
                    o.wait(3000)
                }
            } catch (e: InterruptedException) {
                if (WebDebugger.isDebug) {
                    e.printStackTrace()
                }
                task.out.println("任务执行超时")
            }
        } else {
            try {
                task.run()
            } catch (e: Exception) {
                // 添加异常信息
                e.printStackTrace(task.out)
            }
        }
        // 读取输出的信息
        outContent = task.getOutContent()
    }
}