package com.newman.boidpond

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

class DrawTest(private val mContext: Context) {

    private lateinit var mProgram: GLProgram
    private var mMatrixLoc = 0
    private var mTurnLoc = 0
    private var mSwimLoc = 0
    private var mAnimLoc = 0
    private var mPositionLoc = 0
    private var mVertexBuffer = 0
    private var mElementBuffer = 0

    private var mMVPMatrix = FloatArray(16)

    private val mVertices = ArrayList<Float>()
    private val mIndices = ArrayList<Int>()

    init {
        val n = 8
        val dx = 1f / n.toFloat()

        for (i in 0 .. n) {
            val x = i * dx
            val y = 0.25f
            mVertices.add(x - 0.75f)
            mVertices.add(y)
            mVertices.add(x - 0.75f)
            mVertices.add(-y)
        }

        for (i in 0 until n) {
            val j = 2 * i
            mIndices.add(j)
            mIndices.add(j + 1)
            mIndices.add(j + 3)
            mIndices.add(j)
            mIndices.add(j + 2)
            mIndices.add(j + 3)
        }
    }

    fun glInit() {
        mProgram = GLProgram(R.raw.test_vert, R.raw.test_frag, mContext)

        val buffers = IntArray(2)
        GLES20.glGenBuffers(2, buffers, 0)
        mVertexBuffer = buffers[0]
        mElementBuffer = buffers[1]
    }

    fun start(iMVPMatrix: FloatArray) {
        mMVPMatrix = iMVPMatrix
        mProgram.use()
        mMatrixLoc = mProgram.uniformLocation("uMVPMatrix")
        mTurnLoc = mProgram.uniformLocation("uTurn")
        mSwimLoc = mProgram.uniformLocation("uSwim")
        mAnimLoc = mProgram.uniformLocation("uAnim")
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

    fun draw(pos: FloatArray, size: Float, turn: Float, swim: Float, anim: Float) {
        val matrix = FloatArray(16)
        Matrix.translateM(matrix, 0, mMVPMatrix, 0, pos[0], pos[1], 0f)
        Matrix.scaleM(matrix, 0, size, size, 1f)

        GLES20.glUniformMatrix4fv(mMatrixLoc, 1, false, matrix, 0)
        GLES20.glUniform1f(mTurnLoc, turn)
        GLES20.glUniform1f(mSwimLoc, swim)
        GLES20.glUniform1f(mAnimLoc, anim)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.size, GLES20.GL_UNSIGNED_SHORT, 0)
    }

    fun finish() {
        GLES20.glDisableVertexAttribArray(mPositionLoc)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}