package com.dj.app.webdebugger.library.http.server.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import com.dj.app.webdebugger.library.WebDebugger
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.abs

/**
 * Create by ChenLei on 2020/11/26
 * Describe: 监控布局
 */
internal class MonitorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        // 移动寻找view的敏感度
        private const val MOVE_SENSITIVITY = 5

        // 顶部View
        var topView: View? = null

        /**
         * 获取最顶层的View
         */
        fun obtainTopView(): View? {
            var topView = WebDebugger.topActivity?.window?.decorView
            // 获取顶层window的view
            try {
                val wm =
                    WebDebugger.topActivity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val mGlobal = wm.javaClass.getDeclaredField("mGlobal").apply {
                    isAccessible = true
                }.get(wm)
                val mRoots = mGlobal.javaClass.getDeclaredField("mRoots").apply {
                    isAccessible = true
                }.get(mGlobal)
                val viewList = (mRoots as? ArrayList<*>)?.map {
                    it?.javaClass?.getDeclaredField("mView")?.apply {
                        isAccessible = true
                    }?.get(it) as? ViewGroup
                } ?: ArrayList<ViewGroup>()
                topView = viewList.last() ?: topView
            } catch (e: Exception) {
                if (WebDebugger.isDebug) {
                    e.printStackTrace()
                }
            }
            return topView
        }

        /**
         * 从View中找到hashCode一致的View
         */
        fun findView(view: View, hashCode: Int): View? {
            if (view.hashCode() == hashCode) return view
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val v = findView(view.getChildAt(i), hashCode)
                    if (v != null) {
                        return v
                    }
                }
            }
            return null
        }
    }

    // 边框画笔
    private val mPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#50ff0000")
            style = Paint.Style.FILL_AND_STROKE
        }
    }
    private val mLoc = IntArray(2)
    private val mRect = Rect()

    // 按下的坐标
    private val mDownPoint = Point()

    // 开始移动寻找view了
    private var mMoveFound = false

    // 上一个寻找到的View
    private var mPreTargetView: View?

    // 寻找过的view
    private var mFoundView = HashSet<View>()

    init {
        topView = obtainTopView()
        mPreTargetView = topView
        WebDebugger.viewMonitorObservable.notifyObservers(mPreTargetView?.hashCode())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 将目标View标识出来
        canvas?.drawRect(getViewRect(mPreTargetView ?: return), mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mPreTargetView != null) {
            // 按下
            if (event?.action == MotionEvent.ACTION_DOWN) {
                mDownPoint.set(event.rawX.toInt(), event.rawY.toInt())
                mMoveFound = false
                refresh(findTargetView(mPreTargetView!!, event.rawX.toInt(), event.rawY.toInt()))
            } else if (event?.action == MotionEvent.ACTION_MOVE) {
                // 移动，为了防止太敏感，移动超过一定范围在开始寻找view
                if (mMoveFound ||
                    abs(mDownPoint.x - event.rawX.toInt()) > MOVE_SENSITIVITY ||
                    abs(mDownPoint.y - event.rawY.toInt()) > MOVE_SENSITIVITY
                ) {
                    mMoveFound = true
                    refresh(findTargetView(mPreTargetView!!, event.rawX.toInt(), event.rawY.toInt()))
                }
            }
        }
        return true
    }

    /**
     * 刷新界面
     */
    fun refresh(targetView: View) {
        mPreTargetView = targetView
        WebDebugger.viewMonitorObservable.notifyObservers(mPreTargetView?.hashCode())
        invalidate()
    }

    /**
     * 获取view的坐标信息
     */
    private fun getViewRect(view: View): Rect {
        view.getLocationOnScreen(mLoc)
        mRect.set(mLoc[0], mLoc[1], view.width + mLoc[0], view.height + mLoc[1])
        return mRect
    }

    /**
     * 寻找目标View
     * 寻找的逻辑：
     * * 先从当前View往里找
     * * 如果找不到，会到父布局再往里面找（重复以上直到找到为止）
     */
    private fun findTargetView(currentView: View, x: Int, y: Int): View {
        mFoundView.clear()
        var targetView: View? = null
        // 先往里找
        if (currentView is ViewGroup) {
            targetView = findTargetViewOnInside(currentView, x, y)
        }
        // 如果里面没有
        if (targetView == null) {
            // 往外找
            targetView = findTargetViewOnOutside(currentView, x, y)
        }
        // 如果已经找到最里面，点击的是自己的话
        if (targetView == null && getViewRect(currentView).contains(x, y)) {
            return currentView
        }
        // 如果都不是的话，默认选中最外层
        return targetView ?: topView!!
    }

    /**
     * 往里找
     */
    private fun findTargetViewOnInside(parent: ViewGroup, x: Int, y: Int): View? {
        // 已经找过了,就缓存下来,不需要再找了
        if (mFoundView.contains(parent)) return null
        mFoundView.add(parent)
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            if (getViewRect(view).contains(x, y)) {
                return view
            }
            if (view is ViewGroup) {
                val targetView = findTargetViewOnInside(view, x, y)
                if (targetView != null) {
                    return targetView
                }
            }
        }
        return null
    }

    private fun findTargetViewOnOutside(view: View, x: Int, y: Int): View? {
        val parent = view.parent
        if (parent is ViewGroup) {
            val targetView = findTargetViewOnInside(parent, x, y)
            if (targetView == null && view.parent.parent is ViewGroup) {
                return findTargetViewOnOutside(view.parent.parent as ViewGroup, x, y)
            }
            if (targetView != null) return targetView
        }
        return null
    }
}