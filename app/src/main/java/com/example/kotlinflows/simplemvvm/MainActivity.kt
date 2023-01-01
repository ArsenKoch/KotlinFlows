package com.example.kotlinflows.simplemvvm

import android.os.Bundle
import com.example.kotlinflows.foundation.sideeffects.navigator.plugin.StackFragmentNavigator
import com.example.kotlinflows.foundation.sideeffects.navigator.plugin.NavigatorPlugin
import com.example.kotlinflows.foundation.sideeffects.SideEffectPluginsManager
import com.example.kotlinflows.foundation.sideeffects.dialogs.plugin.DialogsPlugin
import com.example.kotlinflows.foundation.sideeffects.intents.plugin.IntentsPlugin
import com.example.kotlinflows.foundation.sideeffects.permissions.plugin.PermissionsPlugin
import com.example.kotlinflows.foundation.sideeffects.resources.plugin.ResourcesPlugin
import com.example.kotlinflows.foundation.sideeffects.toasts.plugin.ToastsPlugin
import com.example.kotlinflows.foundation.views.activity.BaseActivity
import com.example.kotlinflows.simplemvvm.views.currentcolor.CurrentColorFragment

/**
 * This application is a single-activity app. MainActivity is a container
 * for all screens.
 */
class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) = with(manager) {
        val navigator = createNavigator()
        register(ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragmentContainer,
        defaultTitle = getString(R.string.app_name),
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreenCreator = { CurrentColorFragment.Screen() }
    )
}