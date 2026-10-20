package com.newman.boidpond

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils

class GLTexture(iTextureRes: Int, iContext: Context) {
    private var mTexture = 0
    private var mWidth = 0
    private var mHeight = 0

    var unit = 0
        private set

    init {
        val bitmap = BitmapFactory.decodeResource(iContext.resources, iTextureRes)
        mWidth = bitmap.width
        mHeight = bitmap.height

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        mTexture = textures[0]

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
    }

    fun use(iTextureUnit: Int = -1) {
        if (iTextureUnit >= 0)
            unit = iTextureUnit
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)
    }
}