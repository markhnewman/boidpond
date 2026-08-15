package com.newman.boidpond

import android.content.Context
import android.opengl.GLES20
import java.nio.IntBuffer

class GLProgram(iVertexShaderRes: Int, iFragmentShaderRes: Int, iContext: Context) {
    private val mProgram: Int

    init {
        val vertexShaderFile = iContext.getResources().openRawResource(iVertexShaderRes)
        val vertexShaderCode = vertexShaderFile.bufferedReader().use { it.readText() }

        val fragmentShaderFile = iContext.getResources().openRawResource(iFragmentShaderRes)
        val fragmentShaderCode = fragmentShaderFile.bufferedReader().use { it.readText() }

        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        mProgram = if (getCompileStatus(vertexShader, "Vertex shader")) {
            val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
            if (getCompileStatus(fragmentShader, "Fragment shader")) {
                linkProgram(vertexShader, fragmentShader)
            } else {
                0
            }
        } else {
            0
        }
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun linkProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun getCompileStatus(shader: Int, name: String): Boolean {
        val buffer = IntBuffer.allocate(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, buffer)
        val status = (buffer[0] != 0)
        val shaderInfo  = GLES20.glGetShaderInfoLog(shader)
        println(name +
                (if (status) " compiled successfully" else " compile failed") +
                (if (shaderInfo.isNotEmpty()) " with info log:\n  $shaderInfo" else ""))
        return status
    }

    fun use() {
        GLES20.glUseProgram(mProgram)
    }

    fun uniformLocation(name: String): Int {
        return GLES20.glGetUniformLocation(mProgram, name)
    }

    fun attribLocation(name: String): Int {
        return GLES20.glGetAttribLocation(mProgram, name)
    }
}