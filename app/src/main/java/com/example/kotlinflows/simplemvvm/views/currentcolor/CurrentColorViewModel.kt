package com.example.kotlinflows.simplemvvm.views.currentcolor

import android.Manifest
import com.example.kotlinflows.foundation.model.PendingResult
import com.example.kotlinflows.foundation.model.SuccessResult
import com.example.kotlinflows.foundation.model.takeSuccess
import com.example.kotlinflows.foundation.sideeffects.dialogs.Dialogs
import com.example.kotlinflows.foundation.sideeffects.dialogs.plugin.DialogConfig
import com.example.kotlinflows.foundation.sideeffects.intents.Intents
import com.example.kotlinflows.foundation.sideeffects.navigator.Navigator
import com.example.kotlinflows.foundation.sideeffects.permissions.Permissions
import com.example.kotlinflows.foundation.sideeffects.permissions.plugin.PermissionStatus
import com.example.kotlinflows.foundation.sideeffects.resources.Resources
import com.example.kotlinflows.foundation.sideeffects.toasts.Toasts
import com.example.kotlinflows.foundation.views.BaseViewModel
import com.example.kotlinflows.foundation.views.LiveResult
import com.example.kotlinflows.foundation.views.MutableLiveResult
import com.example.kotlinflows.simplemvvm.R
import com.example.kotlinflows.simplemvvm.model.colors.ColorListener
import com.example.kotlinflows.simplemvvm.model.colors.ColorsRepository
import com.example.kotlinflows.simplemvvm.model.colors.NamedColor
import com.example.kotlinflows.simplemvvm.views.changecolor.ChangeColorFragment
import kotlinx.coroutines.launch

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = resources.getString(R.string.changed_color, result.name)
            toasts.toast(message)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun requestPermission() = viewModelScope.launch {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog())
        } else {
            when (permissions.requestPermission(permission)) {
                PermissionStatus.GRANTED -> {
                    toasts.toast(resources.getString(R.string.permissions_grated))
                }
                PermissionStatus.DENIED -> {
                    toasts.toast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog())) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }

    fun tryAgain() {
        load()
    }

    private fun load() = into(_currentColor) {
        colorsRepository.getCurrentColor()
    }

    private fun createPermissionAlreadyGrantedDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.permissions_already_granted),
        positiveButton = resources.getString(R.string.action_ok)
    )

    private fun createAskForLaunchingAppSettingsDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.open_app_settings_message),
        positiveButton = resources.getString(R.string.action_open),
        negativeButton = resources.getString(R.string.action_cancel)
    )
}