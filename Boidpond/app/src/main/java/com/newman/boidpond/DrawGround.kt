package com.newman.boidpond

import android.content.Context

class DrawGround(private val mContext: Context) {
    private val mDrawShapes = DrawShapes(mContext)

    private var mMVPMatrix = FloatArray(16)

    init {
        /*
        mVertices.add(0f)
        mVertices.add(0f)
        mVertices.add(0f)
        mVertices.add(1f)
        mVertices.add(1f)
        mVertices.add(1f)
        mVertices.add(1f)
        mVertices.add(0f)

        mIndices.add(0)
        mIndices.add(1)
        mIndices.add(2)
        mIndices.add(0)
        mIndices.add(3)
        mIndices.add(2)
         */
    }

    fun glInit() {
        mDrawShapes.glInit()
    }

    fun start(iMVPMatrix: FloatArray) {
        mMVPMatrix = iMVPMatrix
    }

    fun drawRocks(rocks: Tract) {
        for (c in rocks) {
            mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color))
        }
    }

    fun drawPlants(plants: Tract) {
        for (c in plants) {
            mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f * c.color, 0.6f, 0f))
        }
    }

    fun finish() {
        mDrawShapes.flush(mMVPMatrix)
    }
}