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

        mGame.setSize(iWidth, iHeight, mProjectionMatrix)
    }

    override fun draw(iEvents: ArrayList<MyGLTouchEvent>) {
        val dt = mTimer.seconds()

        val controlEvents = ArrayList<MyGLTouchEvent>()
        for (e in iEvents) {
            mGame.touch(e.x, e.y)
        }

        mGame.advance(dt)
        mGame.draw(mProjectionMatrix)
    }
}
