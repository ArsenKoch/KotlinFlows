package com.example.kotlinflows.foundation.sideeffects.resources.plugin

import android.content.Context
import com.example.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.example.kotlinflows.foundation.sideeffects.resources.Resources

class ResourcesSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Resources {

    override fun getString(resId: Int, vararg args: Any): String {
        return appContext.getString(resId, *args)
    }

}