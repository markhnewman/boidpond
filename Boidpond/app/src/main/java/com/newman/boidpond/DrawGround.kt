package com.newman.boidpond

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DrawGround(private val mContext: Context) {
    private val mDrawShapes = DrawShapes(mContext)

    private var mMVPMatrix = FloatArray(16)

    private lateinit var mProgram: GLProgram
    private var mMatrixLoc = 0
    private var mTextureLoc = 0
    private var mColorLoc = 0
    private var mPositionLoc = 0
    private var mVertexBuffer = 0
    private var mElementBuffer = 0
    private var mTexture = 0

    private val mVertices = ArrayList<Float>()
    private val mIndices = ArrayList<Int>()

    init {
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
    }

    fun glInit() {
        mDrawShapes.glInit()

        mProgram = GLProgram(R.raw.ground_vert, R.raw.ground_frag, mContext)

        val buffers = IntArray(2)
        GLES20.glGenBuffers(2, buffers, 0)
        mVertexBuffer = buffers[0]
        mElementBuffer = buffers[1]

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        mTexture = textures[0]

        val bitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.ground)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
    }

    fun start(iMVPMatrix: FloatArray) {
        mMVPMatrix = iMVPMatrix
        mProgram.use()
        mMatrixLoc = mProgram.uniformLocation("uMVPMatrix")
        mTextureLoc = mProgram.uniformLocation("uTexture")
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

        GLES20.glEnableVertexAttribArray(mPositionLoc) // This apparently has to come after glBufferData!
        GLES20.glVertexAttribPointer(mPositionLoc, 2, GLES20.GL_FLOAT, false, 2 * 4, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    fun drawRocks(rocks: Tract) {
        for (c in rocks) {
            GLES20.glUniformMatrix4fv(mMatrixLoc, 1, false, c.matrix, 0)
            GLES20.glUniform1i(mTextureLoc, 0)
            GLES20.glUniform3f(mColorLoc, 0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color)

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.size, GLES20.GL_UNSIGNED_SHORT, 0)

            //mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color))
        }
    }

    fun drawPlants(plants: Tract) {
        for (c in plants) {
            GLES20.glUniformMatrix4fv(mMatrixLoc, 1, false, c.matrix, 0)
            GLES20.glUniform1i(mTextureLoc, 0)
            GLES20.glUniform3f(mColorLoc, 0.4f * c.color, 0.6f, 0f)

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.size, GLES20.GL_UNSIGNED_SHORT, 0)

            //mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f * c.color, 0.6f, 0f))
        }
    }

    fun finish() {
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mPositionLoc)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        //mDrawShapes.flush(mMVPMatrix)
    }
}