package com.newman.boidpond

import android.content.Context
import java.util.ArrayList
import kotlin.random.Random

class Game(private val mContext: Context) {
    private val mDrawShapes = DrawShapes(mContext)

    private val mPos = ArrayList<FloatArray>()
    private val mVel = ArrayList<FloatArray>()
    private val mCenter = ArrayList<FloatArray>()
    private val mRadius = ArrayList<Float>()

    fun setSize(width: Int, height: Int) {
        val spacing = 100f
        val count = 100

        val w = (width.toFloat() / spacing).toInt()
        val h = (height.toFloat() / spacing).toInt()

        for (x in 0 .. w) {
            mCenter.add(floatArrayOf(spacing * x.toFloat(), 0.5f * spacing * Random.nextFloat()))
            mRadius.add(spacing * (0.75f + 0.75f * Random.nextFloat()))

            mCenter.add(floatArrayOf(spacing * x.toFloat(), height - 0.5f * spacing * Random.nextFloat()))
            mRadius.add(spacing * (0.75f + 0.75f * Random.nextFloat()))
        }
        for (y in 1 until h) {
            mCenter.add(floatArrayOf(0.5f * spacing * Random.nextFloat(), spacing * y.toFloat()))
            mRadius.add(spacing * (0.75f + 0.75f * Random.nextFloat()))

            mCenter.add(floatArrayOf(width - 0.5f * spacing * Random.nextFloat(), spacing * y.toFloat()))
            mRadius.add(spacing * (0.75f + 0.75f * Random.nextFloat()))
        }

        for (i in 0 until 10) {
            mCenter.add(floatArrayOf(width * Random.nextFloat(), height * Random.nextFloat()))
            mRadius.add(spacing * (0.5f + 0.5f * Random.nextFloat()))
        }

        for (i in 0 until count) {
            var good = false
            while (!good) {
                val pos = floatArrayOf(width * Random.nextFloat(), height * Random.nextFloat())
                good = true
                for (j in mCenter.indices)
                    good = good and (distanceSquared(pos, mCenter[j]) > mRadius[j] * mRadius[j])
                if (good)
                    mPos.add(pos.copyOf())
            }
            mVel.add(randomUnit(2))
        }
    }

    fun glInit() {
        mDrawShapes.glInit()
    }

    fun advance(dt: Float) {
        val repulse = 0.2f
        val repulseDist = 50f

        val neighborDist = 200f
        val neighborAttract = 0.005f
        val neighborAlign = 2f


        for (i in mPos.indices) {
            val l = mVel[i].length()
            var acc = 3f * (1f - l) * mVel[i] / l

            for (j in mCenter.indices) {
                val dir = mPos[i] - mCenter[j]
                val r = dir.length()
                val f = mRadius[j] + repulseDist - r
                if (f > 0f)
                    acc = acc + repulse * dir * f / r
            }

            var posTotal = floatArrayOf(0f, 0f)
            var velTotal = floatArrayOf(0f, 0f)
            var total = 0

            for (j in mPos.indices) {
                if (i != j) {
                    val dir = mPos[i] - mPos[j]
                    val r = dir.length()
                    val f = repulseDist - r
                    if (f > 0f)
                        acc = acc + repulse * dir * f / r

                    if (r < neighborDist) {
                        total += 1
                        posTotal = posTotal + mPos[j]
                        velTotal = velTotal + mVel[j]
                    }
                }
            }

            if (total > 0) {
                posTotal = posTotal / total.toFloat()
                velTotal = velTotal / total.toFloat()

                run {
                    val dir = posTotal - mPos[i]
                    val r = dir.length()
                    val f = neighborDist - r
                    if (f > 0f)
                        acc = acc + neighborAttract * dir * f / r
                }

                run {
                    val dir = velTotal - mVel[i]
                    val r = dir.length()
                    acc = acc + neighborAlign * dir
                }
            }

            mVel[i] += dt * acc
            mPos[i] += dt * 100f * mVel[i]
        }
    }

    fun draw(iMVPMatrix: FloatArray) {
        for (i in mCenter.indices) {
            mDrawShapes.circle(mCenter[i][0], mCenter[i][1], mRadius[i], 10f, floatArrayOf(1f, 0f, 0f), floatArrayOf(0f, 0f, 0f))
        }
        for (i in mPos.indices) {
            mDrawShapes.circle(mPos[i][0], mPos[i][1], 25f, 10f, floatArrayOf(0f, 1f, 0f), floatArrayOf(0f, 0f, 0f))
        }

        mDrawShapes.flush(iMVPMatrix)
    }
}