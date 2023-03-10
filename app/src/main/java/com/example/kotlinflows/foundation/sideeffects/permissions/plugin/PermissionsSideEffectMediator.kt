package com.example.kotlinflows.foundation.sideeffects.permissions.plugin

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.kotlinflows.foundation.model.Emitter
import com.example.kotlinflows.foundation.model.ErrorResult
import com.example.kotlinflows.foundation.model.toEmitter
import com.example.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.example.kotlinflows.foundation.sideeffects.permissions.Permissions

class PermissionsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<PermissionsSideEffectImpl>(), Permissions {

    val retainedState = RetainedState()

    override fun hasPermissions(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(permission: String): PermissionStatus =
        suspendCancellableCoroutine { continuation ->
            val emitter = continuation.toEmitter()
            if (retainedState.emitter != null) {
                emitter.emit(ErrorResult(IllegalStateException("Only one permission request can be active")))
                return@suspendCancellableCoroutine
            }
            retainedState.emitter = emitter
            target { implementation ->
                implementation.requestPermission(permission)
            }
        }

    class RetainedState(
        var emitter: Emitter<PermissionStatus>? = null
    )
}