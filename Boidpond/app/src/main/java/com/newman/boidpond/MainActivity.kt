package com.newman.boidpond

import android.opengl.GLES20
import android.opengl.Matrix
import java.util.*
import kotlin.math.*

class MainActivity : MyGLActivity() {
    val mGame = Game(this)

    val mDrawShapes = DrawShapes(this)

    private val mProjectionMatrix = FloatArray(16)

    private val mTimer = FrameTimer()

    override fun glInit() {
        mGame.glInit()

        setRenderMode(true)
    }

    override fun setSize(iWidth: Int, iHeight: Int) {
        Matrix.orthoM(mProjectionMatrix, 0, 0f, iWidth.toFloat(), 0f, iHeight.toFloat(), -1f, 1f)

        GLES20.glViewport(0, 0, iWidth, iHeight)

        mGame.setSize(iWidth, iHeight)
    }

    override fun draw(iEvents: ArrayList<MyGLTouchEvent>) {
        val dt = mTimer.seconds()

        val controlEvents = ArrayList<MyGLTouchEvent>()
        for (e in iEvents) {
        }

        GLES20.glClearColor(0.6f, 0.8f, 1f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        mGame.advance(dt)
        mGame.draw(mProjectionMatrix)
    }
}
