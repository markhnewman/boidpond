package com.newman.boidpond

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLSurfaceView
import java.util.*


class MyGLRenderer(private val mGLActivity: MyGLActivity): GLSurfaceView.Renderer {
    private var mEvents = ArrayList<MyGLTouchEvent>(0)

    fun onTouchEvent(e: MyGLTouchEvent) {
        synchronized(this) {
            mEvents.add(e.copy())
        }
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        mGLActivity.glInit()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        mGLActivity.setSize(width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        val events = ArrayList<MyGLTouchEvent>(0)
        synchronized(this) {
            for (e in mEvents)
                events.add(e.copy())
            mEvents.clear()
        }

        mGLActivity.draw(events)
    }
}