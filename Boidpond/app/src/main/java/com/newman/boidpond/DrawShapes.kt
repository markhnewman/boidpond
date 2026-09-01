package com.newman.boidpond

import android.content.Context
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.*

class DrawShapes(private val mContext: Context) {

    private val kCircleResolution = 32
    private val kCircleX = FloatArray(kCircleResolution)
    private val kCircleY = FloatArray(kCircleResolution)
    private val kFishX = ArrayList<Float>()
    private val kFishY = ArrayList<Float>()

    private lateinit var mProgram: GLProgram
    private val mVertices = ArrayList<Float>()
    private val mColors = ArrayList<Float>()
    private val mIndices = ArrayList<Short>()
    private var mCount = 0

    init {
        for (i in 0 until kCircleResolution) {
            val angle = i * 2.0 * PI / kCircleResolution
            kCircleX[i] = cos(angle).toFloat()
            kCircleY[i] = sin(angle).toFloat()
        }

        val fishX = floatArrayOf(0f, 6f, 8f, 10f, 10f, 8f, 6f)
        val fishY = floatArrayOf(0f, 2f, 2f, 1f, -1f, -2f, -2f)
        for (i in fishX.indices) {
            kFishX.add((fishX[i] - 5f) / 10f)
            kFishY.add(fishY[i] / 10f)
        }
    }

    private fun addColor(iColor: FloatArray) {
        mColors.add(iColor[0])
        mColors.add(iColor[1])
        mColors.add(iColor[2])
        if (iColor.size > 3)
            mColors.add(iColor[3])
        else
            mColors.add(1.0f)
    }

    private fun triangleIndices(iA: Int, iB: Int, iC: Int) {
        mIndices.add((mCount + iA).toShort())
        mIndices.add((mCount + iB).toShort())
        mIndices.add((mCount + iC).toShort())
    }

    private fun quadIndices(iA: Int, iB: Int, iC: Int, iD: Int) {
        triangleIndices(iA, iB, iC)
        triangleIndices(iA, iC, iD)
    }

    fun glInit() {
        mProgram = GLProgram(R.raw.shape_vert, R.raw.shape_frag, mContext)
    }

    fun circle(x: Float, y: Float, r: Float, fill: FloatArray) {
        val n = kCircleResolution

        for (i in 0 until n) {
            mVertices.add(x + r * kCircleX[i])
            mVertices.add(y + r * kCircleY[i])
            addColor(fill)
        }

        for (i in 2 until n)
            triangleIndices(0, i - 1, i)
        mCount += n
    }

    fun circle(x: Float, y: Float, r: Float, width: Float, fill: FloatArray, border: FloatArray) {
        val radius = floatArrayOf(r - width, r - width, r)
        val color = arrayOf(fill, border, border)
        val n = kCircleResolution

        for (i in 0 until 3) {
            for (j in 0 until n) {
                mVertices.add(x + radius[i] * kCircleX[j])
                mVertices.add(y + radius[i] * kCircleY[j])
                addColor(color[i])
            }
        }

        for (j in 2 until n)
            triangleIndices(0, j - 1, j)
        mCount += n

        for (j in 1 .. n)
            quadIndices(j - 1, j + n - 1, j.mod(n) + n, j.mod(n))
        mCount += 2 * n
    }

    fun rectangle(left: Float, bottom: Float, right: Float, top: Float, width: Float, fill: FloatArray, border: FloatArray) {
        val x = floatArrayOf(left, right)
        val y = floatArrayOf(bottom, top)
        val xIndex = intArrayOf(0, 1, 1, 0)
        val yIndex = intArrayOf(0, 0, 1, 1)
        val xDir = floatArrayOf(1.0f, -1.0f, -1.0f, 1.0f)
        val yDir = floatArrayOf(1.0f, 1.0f, -1.0f, -1.0f)
        for (i in xIndex.indices) {
            mVertices.add(x[xIndex[i]])
            mVertices.add(y[yIndex[i]])
            for (j in 1..2) {
                addColor(border)
                mVertices.add(x[xIndex[i]] + width * xDir[i])
                mVertices.add(y[yIndex[i]] + width * yDir[i])
            }
            addColor(fill)
            quadIndices(3 * i, 3 * i + 1, 3 * (i + 1).mod(4) + 1, 3 * (i + 1).mod(4))
        }
        quadIndices(2, 5, 8, 11)
        mCount += 12
    }

    fun fish(pos: FloatArray, dir: FloatArray, length: Float, fill: FloatArray) {
        val n = kFishX.size
        val c = length * dir[0]
        val s = length * dir[1]
        for (i in 0 until n) {
            mVertices.add(pos[0] + c * kFishX[i] - s * kFishY[i])
            mVertices.add(pos[1] + s * kFishX[i] + c * kFishY[i])
            addColor(fill)
        }

        for (i in 2 until n)
            triangleIndices(0, i - 1, i)
        mCount += n
    }

    fun flush(iMVPMatrix: FloatArray) {
        val count = mIndices.size
        mCount = 0

        val vertexBuffer = ByteBuffer.allocateDirect(mVertices.size * 4)
        vertexBuffer.order(ByteOrder.nativeOrder())
        val vertices = vertexBuffer.asFloatBuffer()
        for (v in mVertices)
            vertices.put(v)
        vertices.rewind()
        mVertices.clear()

        val colorBuffer = ByteBuffer.allocateDirect(mColors.size * 4)
        colorBuffer.order(ByteOrder.nativeOrder())
        val colors = colorBuffer.asFloatBuffer()
        for (c in mColors)
            colors.put(c)
        colors.rewind()
        mColors.clear()

        val indexBuffer = ByteBuffer.allocateDirect(mIndices.size * 2)
        indexBuffer.order(ByteOrder.nativeOrder())
        val indices = indexBuffer.asShortBuffer()
        for (i in mIndices)
            indices.put(i)
        indices.rewind()
        mIndices.clear()

        mProgram.use()
        val matrixLoc = mProgram.uniformLocation("uMVPMatrix")
        GLES20.glUniformMatrix4fv(matrixLoc, 1, false, iMVPMatrix, 0)

        val positionLoc = mProgram.attribLocation( "aPosition")
        GLES20.glEnableVertexAttribArray(positionLoc)

        GLES20.glVertexAttribPointer(
            positionLoc,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * 4, // 2 coords per vertex, 4 bytes per coord
            vertexBuffer
        )

        val colorLoc = mProgram.attribLocation("aColor")
        GLES20.glEnableVertexAttribArray(colorLoc)

        GLES20.glVertexAttribPointer(
            colorLoc,
            4,
            GLES20.GL_FLOAT,
            false,
            4 * 4, // 4 colors per vertex, 4 bytes per color
            colorBuffer
        )

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionLoc)
        GLES20.glDisableVertexAttribArray(colorLoc)
    }
}