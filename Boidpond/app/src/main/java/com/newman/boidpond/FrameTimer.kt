package com.newman.boidpond

class FrameTimer {
    private var lastTime = System.nanoTime()

    fun seconds(): Float {
        val now = System.nanoTime()
        val deltaNanos = now - lastTime
        lastTime = now
        return deltaNanos / 1_000_000_000f
    }

    fun milliseconds(): Long {
        val now = System.nanoTime()
        val deltaNanos = now - lastTime
        lastTime = now
        return deltaNanos / 1_000_000
    }
}
