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
    var color: FloatArray
)