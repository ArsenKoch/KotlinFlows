package com.example.kotlinflows.foundation.sideeffects.dialogs.plugin

import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.kotlinflows.foundation.model.Emitter
import com.example.kotlinflows.foundation.model.ErrorResult
import com.example.kotlinflows.foundation.model.toEmitter
import com.example.kotlinflows.foundation.sideeffects.SideEffectMediator
import com.example.kotlinflows.foundation.sideeffects.dialogs.Dialogs

class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override suspend fun show(dialogConfig: DialogConfig): Boolean =
       suspendCancellableCoroutine { continuation ->
           val emitter = continuation.toEmitter()
            if (retainedState.record != null) {
                emitter.emit(ErrorResult(IllegalStateException("Can't launch more than 1 dialog at a time")))
                return@suspendCancellableCoroutine
            }

            val wrappedEmitter = Emitter.wrap(emitter) {
                retainedState.record = null
            }

            val record = DialogRecord(wrappedEmitter, dialogConfig)
            wrappedEmitter.setCancelListener {
                target { implementation ->
                    implementation.removeDialog()
                }
            }

            target { implementation ->
                implementation.showDialog(record)
            }

            retainedState.record = record
        }

    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}