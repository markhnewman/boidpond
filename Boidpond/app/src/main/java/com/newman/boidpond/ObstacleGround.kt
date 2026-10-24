package com.newman.boidpond

import android.opengl.Matrix
import kotlin.math.*
import kotlin.random.Random

class ObstacleGround {
    private var mGridSize = 1f
    private var mGridWidth = 0
    private var mGridHeight = 0

    private val mCircles = Tract()

    fun setSize(width: Int, height: Int, iMVPMatrix: FloatArray) {
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
                mCircles.add(
                    Ground(
                        floatArrayOf((i + Random.nextFloat()) * avgR, (j + Random.nextFloat()) * avgR),
                        minR + (maxR - minR) * Random.nextFloat(),
                        Random.nextFloat()
                    )
                )
            }
        }
        mCircles.shuffle(Random)


        mGridSize = 2f * maxR
        mGridWidth = ceil(w / mGridSize).toInt()
        mGridHeight = ceil(h / mGridSize).toInt()
        for (iter in 0 until 10) {
            val grid = ArrayList<ArrayList<ArrayList<Ground>>>()
            for (i in 0 until mGridWidth) {
                val col = ArrayList<ArrayList<Ground>>()
                for (j in 0 until mGridHeight)
                    col.add(ArrayList<Ground>())
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

            Matrix.translateM(c.matrix, 0, iMVPMatrix, 0, c.pos[0], c.pos[1], 0f)

            val d = c.r * randomUnitVec(2)
            val dirMatrix = floatArrayOf(d[0],  d[1], 0f, 0f,
                                         -d[1], d[0], 0f, 0f,
                                         0f,    0f,   1f, 0f,
                                         0f,    0f,   0f, 1f)
            Matrix.multiplyMM(c.matrix, 0, c.matrix, 0, dirMatrix, 0)

            val posMatrix = floatArrayOf(2f,  0f,  0f, 0f,
                                         0f,  2f,  0f, 0f,
                                         0f,  0f,  1f, 0f,
                                         -1f, -1f, 0f, 1f)
            Matrix.multiplyMM(c.matrix, 0, c.matrix, 0, posMatrix, 0)
        }

    }

    fun draw(obstacles: ArrayList<Obstacle>): Pair<Tract, Tract> {
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

        val plants = ArrayList<Ground>()
        val rocks = ArrayList<Ground>()
        for (c in mCircles) {
            var plant = false
            var rock = false
            for (o in grid[c.gridX][c.gridY]) {
                val d = distance(c.pos, o.pos) - o.r + c.r
                plant = plant || (d < -20f)
                rock = rock || (d < 0f)
            }

            if (plant)
                plants.add(c)
            else if (rock)
                rocks.add(c)
        }

        return Pair(plants, rocks)
    }
}