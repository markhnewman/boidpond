package com.newman.boidpond

import android.content.Context
import kotlin.collections.ArrayList
import kotlin.math.*
import kotlin.random.Random

class Game(private val mContext: Context) {
    private val mDrawShapes = DrawShapes(mContext)
    private val mDrawFish = DrawFish(mContext)
    private val mDrawTest = DrawTest(mContext)
    private var mWidth = 0
    private var mHeight = 0
    private var mTime = 0f

    private val mSmallBoids = ArrayList<Boid>()
    private val mMediumBoids = ArrayList<Boid>()
    private val mBigBoids = ArrayList<Boid>()
    private val mObstacles = ArrayList<Obstacle>()
    private val kMoveObstacles = 50
    private var mCurrObstacle = 0

    private val kGridSize = 200f
    private var mGridWidth = 0
    private var mGridHeight = 0
    private val mSmallBoidGrid = ArrayList<ArrayList<ArrayList<Boid>>>()
    private val mMediumBoidGrid = ArrayList<ArrayList<ArrayList<Boid>>>()
    private val mBigBoidGrid = ArrayList<ArrayList<ArrayList<Boid>>>()
    private val mObstacleGrid = ArrayList<ArrayList<ArrayList<Obstacle>>>()

    fun setSize(width: Int, height: Int) {
        if (width == mWidth && height == mHeight) return
        mWidth = width
        mHeight = height

        mGridWidth = ceil(width / kGridSize).toInt()
        mGridHeight = ceil(height / kGridSize).toInt()

        val spacing = 100f
        val smallCount = 300
        val mediumCount = 50
        val bigCount = 10
/*
        val smallCount = 1000
        val mediumCount = 0
        val bigCount = 5
*/

        val w = (width.toFloat() / spacing).toInt()
        val h = (height.toFloat() / spacing).toInt()

        mObstacles.clear()
        mCurrObstacle = 10
        for (i in 0 until mCurrObstacle) {
            mObstacles.add(Obstacle(
                floatArrayOf(width * Random.nextFloat(), height * Random.nextFloat()),
                spacing * (0.5f + 0.5f * Random.nextFloat()),
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))
        }
        for (i in mCurrObstacle until kMoveObstacles)
            mObstacles.add(Obstacle(
                floatArrayOf( -width.toFloat(), -height.toFloat()),
                0f,
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))

        for (x in 0 .. w) {
            mObstacles.add(Obstacle(
                floatArrayOf(spacing * x.toFloat(), -0.5f * spacing * Random.nextFloat()),
                spacing * (0.75f + 0.75f * Random.nextFloat()),
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))
            mObstacles.add(Obstacle(
                floatArrayOf(spacing * x.toFloat(), height + 0.5f * spacing * Random.nextFloat()),
                spacing * (0.75f + 0.75f * Random.nextFloat()),
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))
        }
        for (y in 0 .. h) {
            mObstacles.add(Obstacle(
                floatArrayOf(-0.5f * spacing * Random.nextFloat(), spacing * y.toFloat()),
                spacing * (0.75f + 0.75f * Random.nextFloat()),
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))
            mObstacles.add(Obstacle(
                floatArrayOf(width + 0.5f * spacing * Random.nextFloat(), spacing * y.toFloat()),
                spacing * (0.75f + 0.75f * Random.nextFloat()),
                floatArrayOf(0.4f * Random.nextFloat(), 0.6f, 0f)
            ))
        }
        mObstacles.subList(kMoveObstacles, mObstacles.size).shuffle(Random)

        mSmallBoids.clear()
        for (i in 0 until smallCount) {
            mSmallBoids.add(Boid(
                newBoid(width, height, mObstacleGrid),
                randomUnitVec(2),
                mix(floatArrayOf(0.5f, 0f, 0f), floatArrayOf(0.4f, 0.2f, 0f), Random.nextFloat())
            ))
        }

        mMediumBoids.clear()
        for (i in 0 until mediumCount) {
            mMediumBoids.add(Boid(
                newBoid(width, height, mObstacleGrid),
                randomUnitVec(2),
                mix(floatArrayOf(0.9f, 0f, 0f), floatArrayOf(0.8f, 0.4f, 0f), Random.nextFloat())
            ))
        }

        mBigBoids.clear()
        for (i in 0 until bigCount) {
            mBigBoids.add(Boid(
                newBoid(width, height, mObstacleGrid),
                randomUnitVec(2),
                mix(floatArrayOf(0.9f, 0.7f, 0f), floatArrayOf(1f, 0.9f, 0.6f), Random.nextFloat())
            ))
        }
    }

    fun glInit() {
        mDrawShapes.glInit()
        mDrawFish.glInit()
        mDrawTest.glInit()
    }

    fun touch(x: FloatArray, y: FloatArray) {
        for (i in x.indices) {
            val touchPos = floatArrayOf(x[i], y[i])
            for (j in 0 until 20) {
                val pos = touchPos + 50f * randomCircleVec(2)
                val r = 75f * (0.5f + 0.5f * Random.nextFloat())

                var good = true
                for (o in mObstacles) {
                    val sq = distanceSquared(pos, o.pos)
                    //good = good and (sq > max(r * r, o.r * o.r))
                    good = good and (sq > 0.7f * max(r * r, o.r * o.r))
                }

                if (good) {
                    mObstacles[mCurrObstacle].pos = pos
                    mObstacles[mCurrObstacle].r = r
                    mCurrObstacle = (mCurrObstacle + 1).mod(kMoveObstacles)
                    break
                }
            }
        }
    }

    fun advance(iDt: Float) {
        val dt = min(iDt, 0.1f)
        mTime += dt

        gridBoids(mSmallBoids, mSmallBoidGrid)
        gridBoids(mMediumBoids, mMediumBoidGrid)
        gridBoids(mBigBoids, mBigBoidGrid)
        gridObstacles(mObstacles, mObstacleGrid)

        val smallAccel = ArrayList<FloatArray>()
        for (boid in mSmallBoids) {
            val x = floor(boid.pos[0] / kGridSize).toInt()
            val y = floor(boid.pos[1] / kGridSize).toInt()
            if (x < 0 || x >= mGridWidth || y < 0 || y >= mGridHeight) {
                smallAccel.add(zeroVec(2))
                continue
            }
            var acc = maintainSpeed(3f, 1f, boid)
            acc += avoidObstacles(40f, 0.2f, 20f, boid, mObstacleGrid[x][y])
            acc += avoidBoids(40f, 0.2f, boid, mSmallBoidGrid[x][y])
            acc += avoidBoids(80f, 0.2f, boid, mMediumBoidGrid[x][y])
            acc += avoidBoids(150f, 0.2f, boid, mBigBoidGrid[x][y])
            acc += averageNeighbor(200f, 0.005f, 2f, boid, mSmallBoidGrid[x][y])
            smallAccel.add(acc)
        }

        val mediumAccel = ArrayList<FloatArray>()
        for (boid in mMediumBoids) {
            val x = floor(boid.pos[0] / kGridSize).toInt()
            val y = floor(boid.pos[1] / kGridSize).toInt()
            if (x < 0 || x >= mGridWidth || y < 0 || y >= mGridHeight) {
                mediumAccel.add(zeroVec(2))
                continue
            }
            var acc = maintainSpeed(3f, 0.9f, boid)
            acc += avoidObstacles(50f, 0.2f, 20f, boid, mObstacleGrid[x][y])
            acc += avoidBoids(60f, 0.1f, boid, mSmallBoidGrid[x][y])
            acc += avoidBoids(60f, 0.2f, boid, mMediumBoidGrid[x][y])
            acc += avoidBoids(120f, 0.2f, boid, mBigBoidGrid[x][y])
            acc += averageNeighbor(200f, 0.003f, 1f, boid, mMediumBoidGrid[x][y])
            mediumAccel.add(acc)
        }

        val bigAccel = ArrayList<FloatArray>()
        for (boid in mBigBoids) {
            val x = floor(boid.pos[0] / kGridSize).toInt()
            val y = floor(boid.pos[1] / kGridSize).toInt()
            if (x < 0 || x >= mGridWidth || y < 0 || y >= mGridHeight) {
                bigAccel.add(zeroVec(2))
                continue
            }
            var acc = maintainSpeed(3f, 0.8f, boid)
            acc += avoidObstacles(100f, 0.04f, 20f, boid, mObstacleGrid[x][y])
            acc += avoidBoids(100f, 0.1f, boid, mBigBoidGrid[x][y])
            acc += averageNeighbor(200f, 0f, 0.5f, boid, mBigBoidGrid[x][y])
            bigAccel.add(acc)
        }

        for (i in mSmallBoids.indices) {
            mSmallBoids[i].vel += dt * smallAccel[i]
            mSmallBoids[i].pos += dt * 100f * mSmallBoids[i].vel
            animate(mSmallBoids[i], dt, 1f)
        }
        for (i in mMediumBoids.indices) {
            mMediumBoids[i].vel += dt * mediumAccel[i]
            mMediumBoids[i].pos += dt * 100f * mMediumBoids[i].vel
            animate(mMediumBoids[i], dt, 0.75f)
        }
        for (i in mBigBoids.indices) {
            mBigBoids[i].vel += dt * bigAccel[i]
            mBigBoids[i].pos += dt * 100f * mBigBoids[i].vel
            animate(mBigBoids[i], dt, 0.5f)
        }
    }

    fun draw(iMVPMatrix: FloatArray) {
        for (o in mObstacles) {
            mDrawShapes.circle(o.pos[0], o.pos[1], o.r, floatArrayOf(0f, 0f, 0f))
        }
        for (o in mObstacles) {
            mDrawShapes.circle(o.pos[0], o.pos[1], o.r - 10f, o.color)
        }
        mDrawShapes.flush(iMVPMatrix)

        mDrawFish.start(iMVPMatrix)
        for (b in mSmallBoids) {
            mDrawFish.draw(b, 30f)
        }
        for (b in mMediumBoids) {
            mDrawFish.draw(b, 50f)
        }
        for (b in mBigBoids) {
            mDrawFish.draw(b, 100f)
        }
        mDrawFish.finish()

        /*mDrawTest.start(iMVPMatrix)
        mDrawTest.draw(floatArrayOf(0.5f * mWidth, 0.5f * mHeight), 400f, sin(0.5f * mTime).pow(3f), 0.6f, 1.0f * mTime)
        mDrawTest.finish()*/
    }

    private fun newBoid(width: Int, height: Int, grid: ArrayList<ArrayList<ArrayList<Obstacle>>>): FloatArray {
        while (true) {
            val pos = floatArrayOf(width * Random.nextFloat(), height * Random.nextFloat())
            var good = true
            for (j in mObstacles.indices)
                good = good and (distanceSquared(pos, mObstacles[j].pos) > mObstacles[j].r * mObstacles[j].r)
            if (good)
                return  pos
        }

    }

    private fun gridObstacles(obstacles: ArrayList<Obstacle>, grid: ArrayList<ArrayList<ArrayList<Obstacle>>>) {
        grid.clear()
        for (i in 0 until mGridWidth) {
            val col = ArrayList<ArrayList<Obstacle>>()
            for (j in 0 until mGridHeight)
                col.add(ArrayList<Obstacle>())
            grid.add(col)
        }

        for (i in obstacles.indices) {
            val o = obstacles[i]
            val left = max(floor((o.pos[0] - o.r) / kGridSize).toInt() - 1, 0)
            val right = min(floor((o.pos[0] + o.r) / kGridSize).toInt() + 2, mGridWidth)
            val bottom = max(floor((o.pos[1] - o.r) / kGridSize).toInt() - 1, 0)
            val top = min(floor((o.pos[1] + o.r) / kGridSize).toInt() + 2, mGridHeight)
            for (x in left until right)
                for (y in bottom until top)
                    grid[x][y].add(o)
        }
    }

    private fun gridBoids(boids: ArrayList<Boid>, grid: ArrayList<ArrayList<ArrayList<Boid>>>) {
        grid.clear()
        for (i in 0 until mGridWidth) {
            val col = ArrayList<ArrayList<Boid>>()
            for (j in 0 until mGridHeight)
                col.add(ArrayList<Boid>())
            grid.add(col)
        }

        for (i in boids.indices) {
            val b = boids[i]
            val left = max(floor(b.pos[0] / kGridSize).toInt() - 1, 0)
            val right = min(floor(b.pos[0] / kGridSize).toInt() + 2, mGridWidth)
            val bottom = max(floor(b.pos[1] / kGridSize).toInt() - 1, 0)
            val top = min(floor(b.pos[1] / kGridSize).toInt() + 2, mGridHeight)
            for (x in left until right)
                for (y in bottom until top)
                    grid[x][y].add(b)
        }
    }

    private fun maintainSpeed(gain: Float, target: Float, self: Boid): FloatArray {
        val l = self.vel.length()
        return gain * (target - l) * self.vel / l
    }

    private fun capSpeed(gain: Float, target: Float, self: Boid): FloatArray {
        val l = self.vel.length()
        if (l < target) return zeroVec(self.vel.size)
        return gain * (target - l) * self.vel / l
    }

    private fun avoidObstacles(dist: Float, repulse: Float, inverse: Float, self: Boid, obstacles: ArrayList<Obstacle>): FloatArray {
        var acc = zeroVec(2)
        for (o in obstacles) {
            val dir = self.pos - o.pos
            val r = dir.length()
            val f = o.r + dist - r
            if (f > 0f) {
                val i = max(r - o.r,0.5f)
                acc += (repulse * f + inverse / i) * dir / r
            }
        }
        return acc
    }

    private fun avoidBoids(dist: Float, repulse: Float, self: Boid, boids: ArrayList<Boid>): FloatArray {
        var acc = zeroVec(2)
        for (b in boids) {
            if (b !== self) {
                val dir = self.pos - b.pos
                val r = dir.length()
                val f = dist - r
                if (f > 0f)
                    acc += repulse * dir * f / r
            }
        }
        return acc
    }

    private fun averageNeighbor(dist: Float, attract: Float, align: Float, self: Boid, boids: ArrayList<Boid>): FloatArray {
        var acc = zeroVec(2)
        var posTotal = zeroVec(2)
        var velTotal = zeroVec(2)
        var total = 0

        for (b in boids) {
            if (b !== self) {
                val dir = self.pos - b.pos
                val r = dir.length()
                if (r < dist) {
                    total += 1
                    posTotal += b.pos
                    velTotal += b.vel
                }
            }
        }

        if (total > 0) {
            posTotal /= total.toFloat()
            velTotal /= total.toFloat()

            val dir = posTotal - self.pos
            val r = dir.length()
            val f = dist - r
            if (f > 0f)
                acc += attract * dir * f / r

            val diff = velTotal - self.vel
            acc += align * diff
        }

        return acc
    }

    private fun animate(boid: Boid, dt: Float, swim: Float) {
        val speed = 1.0f * exp(boid.vel.length())
        boid.anim = (boid.anim + swim * speed * dt) % 1f
        boid.swim = 0.3f * speed

        val dir = boid.vel.normalize()
        val turn = -10f * cross2D(dir, boid.prevVel)
        boid.turn += (turn - boid.turn) * (1f - exp(-5f * dt))
        boid.prevVel = dir

        boid.turn = max(min(boid.turn, 4f), -4f)
        boid.swim = min(boid.swim, 2f)
    }
}