package com.example.kotlinflows.foundation.sideeffects.resources.plugin

import android.content.Context
import com.example.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.example.kotlinflows.foundation.sideeffects.SideEffectPlugin
import com.example.kotlinflows.foundation.sideeffects.resources.Resources

/**
 * Plugin for accessing app resources from view-models.
 * Allows adding [Resources] interface to the view-model constructor.
 */
class ResourcesPlugin : SideEffectPlugin<ResourcesSideEffectMediator, Nothing> {

    override val mediatorClass: Class<ResourcesSideEffectMediator>
        get() = ResourcesSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ResourcesSideEffectMediator(applicationContext)
    }

}