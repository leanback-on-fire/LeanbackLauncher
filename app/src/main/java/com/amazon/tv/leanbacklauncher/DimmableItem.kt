package com.amazon.tv.leanbacklauncher

import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState

interface DimmableItem {

    fun setDimState(dimState: DimState, z: Boolean)

}