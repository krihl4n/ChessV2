package com.krihl4n

class Dependencies {

    companion object  {
        lateinit var positionTracker: PositionTracker
    }

    init {
        positionTracker = PositionTracker()
    }
}