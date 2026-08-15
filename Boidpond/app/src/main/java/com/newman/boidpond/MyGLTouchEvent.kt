package com.newman.boidpond

const val TOUCH_DOWN =          0x0001  // Any finger has started
const val TOUCH_FIRST_DOWN =    0x0002  // The finger that started was the first in the touch
const val TOUCH_MULTI_DOWN =    0x0004  // The finger that started was not the first in the touch
const val TOUCH_UP =            0x0010  // Any finger has lifted
const val TOUCH_FIRST_UP =      0x0020  // The finger that lifted was the first to start in the touch
const val TOUCH_MULTI_UP =      0x0040  // The finger that lifted was not the first to start in the touch
const val TOUCH_LAST_UP =       0x0080  // The finger that lifted was the last remaining in the touch
const val TOUCH_MOVE =          0x0100  // Any finger has moved
const val TOUCH_FIRST_MOVE =    0x0200  // The finger that moved was the first in the touch
const val TOUCH_MULTI_MOVE =    0x0400  // The finger that moved was not the first in the touch

data class MyGLTouchEvent(val action: Int, val x: FloatArray, val y: FloatArray)
