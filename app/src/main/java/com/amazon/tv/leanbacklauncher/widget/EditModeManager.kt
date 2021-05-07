package com.amazon.tv.leanbacklauncher.widget

import com.amazon.tv.leanbacklauncher.widget.EditModeManager

class EditModeManager {
    var selectedComponentName: String? = null

    companion object {
        val instance = EditModeManager()
    }
}