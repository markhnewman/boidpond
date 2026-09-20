package com.newman.boidpond

import android.content.Context
import kotlin.math.*
import kotlin.random.Random

class DrawGround(private val mContext: Context) {
    private val mDrawShapes = DrawShapes(mContext)

    private var mGridSize = 1f
    private var mGridWidth = 0
    private var mGridHeight = 0


    data class Circle(
        var pos: FloatArray,
        var r: Float,
        var color: Float,

        var gridX: Int,
        var gridY: Int
    ) {
        constructor(pos: FloatArray, r: Float, color: Float) : this(pos, r, color, 0, 0)
    }
    private val mCircles = ArrayList<Circle>()

    fun setSize(width: Int, height: Int) {
        val minR = 15f
        val maxR = 25f

        mCircles.clear()

        val w = width.toFloat()
        val h = height.toFloat()
        val avgR = sqrt(minR * maxR)
        val xCount = ceil(w / avgR).toInt()
        val yCount = ceil(h / avgR).toInt()
        for (i in 0 until xCount) {
            for (j in 0 until yCount) {
                mCircles.add(Circle(
                    floatArrayOf((i + Random.nextFloat()) * avgR, (j + Random.nextFloat()) * avgR),
//                    floatArrayOf(i * avgR, j * avgR),
                    minR + (maxR - minR) * Random.nextFloat(),
                    Random.nextFloat()
                ))
            }
        }
        mCircles.shuffle(Random)


        mGridSize = 2f * maxR
        mGridWidth = ceil(w / mGridSize).toInt()
        mGridHeight = ceil(h / mGridSize).toInt()
        for (iter in 0 until 10) {
            val grid = ArrayList<ArrayList<ArrayList<Circle>>>()
            for (i in 0 until mGridWidth) {
                val col = ArrayList<ArrayList<Circle>>()
                for (j in 0 until mGridHeight)
                    col.add(ArrayList<Circle>())
                grid.add(col)
            }

            for (c in mCircles) {
                if (c.pos[0] < 0f) c.pos[0] = 0f
                if (c.pos[0] > w - 1) c.pos[0] = w - 1
                if (c.pos[1] < 0f) c.pos[1] = 0f
                if (c.pos[1] > h - 1) c.pos[1] = h - 1

                val left = max(floor(c.pos[0] / mGridSize).toInt() - 1, 0)
                val right = min(floor(c.pos[0] / mGridSize).toInt() + 2, mGridWidth)
                val bottom = max(floor(c.pos[1] / mGridSize).toInt() - 1, 0)
                val top = min(floor(c.pos[1] / mGridSize).toInt() + 2, mGridHeight)
                for (x in left until right)
                    for (y in bottom until top)
                        grid[x][y].add(c)
            }

            val move = ArrayList<FloatArray>()
            for (c in mCircles) {
                val x = floor(c.pos[0] / mGridSize).toInt()
                val y = floor(c.pos[1] / mGridSize).toInt()
                var m = zeroVec(2)
                for (d in grid[x][y]) {
                    if (c !== d) {
                        val dir = c.pos - d.pos
                        val r = dir.length()
                        val f = c.r + d.r - r
                        if (f > 0f)
                            m += 0.1f * f * dir / r
                    }
                }
                move.add(m)
            }

            for (i in mCircles.indices) {
                val c = mCircles[i]
                c.pos += move[i]
            }
        }

        for (c in mCircles) {
            if (c.pos[0] < 0f) c.pos[0] = 0f
            if (c.pos[0] > w - 1) c.pos[0] = w - 1
            if (c.pos[1] < 0f) c.pos[1] = 0f
            if (c.pos[1] > h - 1) c.pos[1] = h - 1

            c.gridX = floor(c.pos[0] / mGridSize).toInt()
            c.gridY = floor(c.pos[1] / mGridSize).toInt()
        }

    }

    fun glInit() {
        mDrawShapes.glInit()
    }

    fun draw(obstacles: ArrayList<Obstacle>, iMVPMatrix: FloatArray) {
        val grid = ArrayList<ArrayList<ArrayList<Obstacle>>>()

        for (i in 0 until mGridWidth) {
            val col = ArrayList<ArrayList<Obstacle>>()
            for (j in 0 until mGridHeight)
                col.add(ArrayList<Obstacle>())
            grid.add(col)
        }

        for (i in obstacles.indices) {
            val o = obstacles[i]
            val left = max(floor((o.pos[0] - o.r) / mGridSize).toInt() - 1, 0)
            val right = min(floor((o.pos[0] + o.r) / mGridSize).toInt() + 2, mGridWidth)
            val bottom = max(floor((o.pos[1] - o.r) / mGridSize).toInt() - 1, 0)
            val top = min(floor((o.pos[1] + o.r) / mGridSize).toInt() + 2, mGridHeight)
            for (x in left until right)
                for (y in bottom until top)
                    grid[x][y].add(o)
        }

        val greenCircles = ArrayList<Circle>()
        val rockCircles = ArrayList<Circle>()
        for (c in mCircles) {
            var green = false
            var rock = false
            for (o in grid[c.gridX][c.gridY]) {
                val d = distance(c.pos, o.pos) - o.r + c.r
                green = green || (d < -20f)
                rock = rock || (d < 0f)
            }

            if (green)
                greenCircles.add(c)
            else if (rock)
                rockCircles.add(c)
        }

        for (c in greenCircles) {
            mDrawShapes.circle(c.pos[0], c.pos[1], c.r, 0.6f * floatArrayOf(0.4f * c.color, 0.6f, 0f))
        }
        for (c in rockCircles) {
            mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color, 0.4f + 0.2f * c.color))
        }
        for (c in greenCircles) {
            mDrawShapes.circle(c.pos[0], c.pos[1], c.r, floatArrayOf(0.4f * c.color, 0.6f, 0f))
        }
        mDrawShapes.flush(iMVPMatrix)
    }
}