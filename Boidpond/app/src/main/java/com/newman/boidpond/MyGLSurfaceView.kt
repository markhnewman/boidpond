package com.newman.boidpond

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import java.util.*

class MyGLSurfaceView(iGLActivity: MyGLActivity) : GLSurfaceView(iGLActivity) {

    private val mRenderer = MyGLRenderer(iGLActivity)

    private var mFirstDown = false
    private var mFirstID = 0

    private var mLastX = ArrayList<Float>()
    private var mLastY = ArrayList<Float>()

    init {
        setEGLContextClientVersion(2)

        setRenderer(mRenderer)

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = ArrayList<Float>()
        val y = ArrayList<Float>()
        for(i in 0 until e.pointerCount) {
            x.add(e.getX(i))
            y.add(height - e.getY(i))
        }

        var action = 0
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                action = TOUCH_DOWN or TOUCH_FIRST_DOWN
                mFirstDown = true
                mFirstID = e.getPointerId(0)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                action = TOUCH_DOWN or TOUCH_MULTI_DOWN
            }
            MotionEvent.ACTION_UP -> {
                action = TOUCH_UP or TOUCH_LAST_UP
                action = action or if (mFirstDown && e.getPointerId(0) == mFirstID) {
                    mFirstDown = false
                    TOUCH_FIRST_UP
                } else
                    TOUCH_MULTI_UP
            }
            MotionEvent.ACTION_POINTER_UP -> {
                action = TOUCH_UP
                action = action or if (mFirstDown && e.getPointerId(0) == mFirstID) {
                    mFirstDown = false
                    TOUCH_FIRST_UP
                } else
                    TOUCH_MULTI_UP
            }
            MotionEvent.ACTION_MOVE -> {
                action = TOUCH_MOVE
                for (i in x.indices) {
                    if (x[i] != mLastX[i] || y[i] != mLastY[i]) {
                        action = action or if (mFirstDown && e.getPointerId(i) == mFirstID)
                            TOUCH_FIRST_MOVE
                        else
                            TOUCH_MULTI_MOVE
                    }
                }
            }
        }
        mLastX = x
        mLastY = y

        mRenderer.onTouchEvent(MyGLTouchEvent(action, x.toFloatArray(), y.toFloatArray()))
        requestRender()
        return true
    }
}
