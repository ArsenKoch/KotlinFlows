package com.example.kotlinflows.foundation.sideeffects.intents

import com.example.kotlinflows.foundation.sideeffects.intents.plugin.IntentsPlugin

/**
 * Side-effects interface for launching some system activities.
 * You need to add [IntentsPlugin] to your activity before using this feature.
 */
interface Intents {

    /**
     * Open system settings for this application.
     */
    fun openAppSettings()

}