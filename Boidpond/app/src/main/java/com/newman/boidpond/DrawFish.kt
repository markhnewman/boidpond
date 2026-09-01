package com.newman.boidpond

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

class DrawFish(private val mContext: Context) {

    private lateinit var mProgram: GLProgram
    private var mMatrixLoc = 0
    private var mColorLoc = 0
    private var mPositionLoc = 0
    private var mVertexBuffer = 0
    private var mElementBuffer = 0

    private var mMVPMatrix = FloatArray(16)

    private val mVertices = ArrayList<Float>()
    private val mIndices = ArrayList<Int>()

    init {
        val n = 6
        val dx = 1f / n.toFloat()

        mVertices.add(-0.5f)
        mVertices.add(0f)
        for (i in 1 until n) {
            val x = 1f - (1f - i * dx).pow(2f)
            val y = 0.35f * x * sqrt(1f - x)
            mVertices.add(x - 0.5f)
            mVertices.add(y)
            mVertices.add(x - 0.5f)
            mVertices.add(-y)
        }
        mVertices.add(0.5f)
        mVertices.add(0f)

        mIndices.add(0)
        mIndices.add(1)
        mIndices.add(2)
        for (i in 1 until n - 1) {
            val j = 2 * i - 1
            mIndices.add(j)
            mIndices.add(j + 1)
            mIndices.add(j + 3)
            mIndices.add(j)
            mIndices.add(j + 2)
            mIndices.add(j + 3)
        }
        mIndices.add(2 * n - 1)
        mIndices.add(2 * n - 2)
        mIndices.add(2 * n - 3)
    }

    fun glInit() {
        mProgram = GLProgram(R.raw.fish_vert, R.raw.fish_frag, mContext)

        val buffers = IntArray(2)
        GLES20.glGenBuffers(2, buffers, 0)
        mVertexBuffer = buffers[0]
        mElementBuffer = buffers[1]
    }

    fun start(iMVPMatrix: FloatArray) {
        mMVPMatrix = iMVPMatrix
        mProgram.use()
        mMatrixLoc = mProgram.uniformLocation("uMVPMatrix")
        mColorLoc = mProgram.uniformLocation("uColor")
        mPositionLoc = mProgram.attribLocation("aPosition")

        val vertexSize = mVertices.size * 4
        val vertexBuffer = ByteBuffer.allocateDirect(vertexSize)
        vertexBuffer.order(ByteOrder.nativeOrder())
        val vertices = vertexBuffer.asFloatBuffer()
        for (v in mVertices)
            vertices.put(v)
        vertices.rewind()
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBuffer)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexSize, vertices, GLES20.GL_STATIC_DRAW)

        val indexSize = mIndices.size * 2
        val indexBuffer = ByteBuffer.allocateDirect(indexSize)
        indexBuffer.order(ByteOrder.nativeOrder())
        val indices = indexBuffer.asShortBuffer()
        for (i in mIndices)
            indices.put(i.toShort())
        indices.rewind()
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBuffer)
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexSize, indices, GLES20.GL_STATIC_DRAW)

        GLES20.glEnableVertexAttribArray(mPositionLoc) // This apparently has to come after glBufferData!!!
        GLES20.glVertexAttribPointer(mPositionLoc, 2, GLES20.GL_FLOAT, false, 2 * 4, 0)
    }

    fun draw(pos: FloatArray, dir: FloatArray, length: Float, fill: FloatArray) {
        val matrix = FloatArray(16)
        Matrix.translateM(matrix, 0, mMVPMatrix, 0, pos[0], pos[1], 0f)

        val d = length * dir
        val dirMatrix = floatArrayOf(d[0],  d[1], 0f, 0f,
                                     -d[1], d[0], 0f, 0f,
                                     0f,    0f,     1f, 0f,
                                     0f,    0f,     0f, 1f)
        Matrix.multiplyMM(matrix, 0, matrix, 0, dirMatrix, 0)

        GLES20.glUniformMatrix4fv(mMatrixLoc, 1, false, matrix, 0)
        GLES20.glUniform4f(mColorLoc, fill[0], fill[1], fill[2], 1f)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.size, GLES20.GL_UNSIGNED_SHORT, 0)
    }

    fun finish() {
        GLES20.glDisableVertexAttribArray(mPositionLoc)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}