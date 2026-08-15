package com.newman.boidpond

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

abstract class MyGLActivity : AppCompatActivity() {
    private lateinit var mGLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)
    }

    open fun glInit() {}
    open fun setSize(iWidth: Int, iHeight: Int) {}
    open fun draw(iEvents: ArrayList<MyGLTouchEvent>) {}

    fun setRenderMode(iRenderContinuously: Boolean) {
        if (iRenderContinuously)
            mGLView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        else
            mGLView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}