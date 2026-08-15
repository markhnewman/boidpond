package com.newman.boidpond

import kotlin.math.*
import kotlin.random.Random

val kArbitrary3DUnit = floatArrayOf(0.36f, 0.48f, 0.8f)

operator fun FloatArray.plus(iRhs: FloatArray): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] + iRhs[i]
    return r
}

operator fun FloatArray.minus(iRhs: FloatArray): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] - iRhs[i]
    return r
}

operator fun FloatArray.unaryMinus(): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = -this[i]
    return r
}

operator fun FloatArray.times(iRhs: FloatArray): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] * iRhs[i]
    return r
}

operator fun FloatArray.times(iRhs: Float): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] * iRhs
    return r
}

operator fun Float.times(iRhs: FloatArray): FloatArray {
    val r = FloatArray(iRhs.size)
    for (i in iRhs.indices)
        r[i] = this * iRhs[i]
    return r
}

operator fun FloatArray.div(iRhs: FloatArray): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] / iRhs[i]
    return r
}

operator fun FloatArray.div(iRhs: Float): FloatArray {
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] / iRhs
    return r
}

operator fun Float.div(iRhs: FloatArray): FloatArray {
    val r = FloatArray(iRhs.size)
    for (i in iRhs.indices)
        r[i] = this / iRhs[i]
    return r
}

fun dot(iLhs: FloatArray, iRhs: FloatArray): Float {
    var r = 0f
    for (i in iLhs.indices)
        r += iLhs[i] * iRhs[i]
    return r
}

fun cross(iLhs: FloatArray, iRhs: FloatArray): FloatArray {
    return floatArrayOf(
        iLhs[1] * iRhs[2] - iLhs[2] * iRhs[1],
        iLhs[2] * iRhs[0] - iLhs[0] * iRhs[2],
        iLhs[0] * iRhs[1] - iLhs[1] * iRhs[0]
    )
}

fun FloatArray.length(): Float {
    var r = 0f
    for (i in this.indices)
        r += this[i] * this[i]
    return sqrt(r)
}

fun FloatArray.lengthSquared(): Float {
    var r = 0f
    for (i in this.indices)
        r += this[i] * this[i]
    return r
}

fun FloatArray.normalize(): FloatArray {
    var l = 0f
    for (i in this.indices)
        l += this[i] * this[i]
    if (l == 0f)
        return this
    l = sqrt(l)
    val r = FloatArray(this.size)
    for (i in this.indices)
        r[i] = this[i] / l
    return r
}

fun FloatArray.distanceTo(iRhs: FloatArray): Float {
    var r = 0f
    for (i in this.indices) {
        val d = iRhs[i] - this[i]
        r += d * d
    }
    return sqrt(r)
}

fun distance(iLhs: FloatArray, iRhs: FloatArray): Float {
    var r = 0f
    for (i in iLhs.indices) {
        val d = iRhs[i] - iLhs[i]
        r += d * d
    }
    return sqrt(r)
}

fun FloatArray.distanceToSquared(iRhs: FloatArray): Float {
    var r = 0f
    for (i in this.indices) {
        val d = iRhs[i] - this[i]
        r += d * d
    }
    return r
}

fun distanceSquared(iLhs: FloatArray, iRhs: FloatArray): Float {
    var r = 0f
    for (i in iLhs.indices) {
        val d = iRhs[i] - iLhs[i]
        r += d * d
    }
    return r
}

fun rotate(iVec: FloatArray, iAxisX: FloatArray, iAxisY: FloatArray, iAngle: Float): FloatArray {
    val c = cos(iAngle)
    val s = sin(iAngle)
    val x = dot(iVec, iAxisX)
    val y = dot(iVec, iAxisY)
    val v = iVec - x * iAxisX - y * iAxisY
    return v + (x * c - y * s) * iAxisX + (x * s + y * c) * iAxisY
}

fun randomUnit(iDim: Int): FloatArray {
    while (true) {
        val unit = FloatArray(iDim)
        var r = 0f
        for (i in unit.indices) {
            unit[i] = Random.nextDouble(-1.0, 1.0).toFloat()
            r += unit[i] * unit[i]
        }
        if (r < 1f) {
            r = sqrt(r)
            for (i in unit.indices) {
                unit[i] /= r
            }
            return unit
        }
    }
}

fun floatArraytoString(iVec: FloatArray): String {
    return (0 until iVec.size).joinToString(", ", "[", "]") { iVec[it].toString() }
}
