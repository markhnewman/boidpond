package com.newman.boidpond

data class Boid(
    var pos: FloatArray,
    var vel: FloatArray,
    var color: FloatArray,

    var turn: Float = 0f,
    var swim: Float = 0f,
    var anim: Float = 0f,

    var prevVel: FloatArray = floatArrayOf(0f, 0f)
)

data class Obstacle(
    var pos: FloatArray,
    var r: Float,
    var color: FloatArray,
    var targetR: Float
) {
    constructor(pos: FloatArray, r: Float, color: FloatArray) : this(pos, r, color, r)
}

data class Ground(
    var pos: FloatArray,
    var r: Float,
    var color: Float,

    var gridX: Int,
    var gridY: Int,

    val matrix: FloatArray
) {
    constructor(pos: FloatArray, r: Float, color: Float) : this(pos, r, color, 0, 0, FloatArray(16))
}
typealias Tract = ArrayList<Ground>

