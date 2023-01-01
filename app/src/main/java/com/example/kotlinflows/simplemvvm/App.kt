package com.example.kotlinflows.simplemvvm

import android.app.Application
import kotlinx.coroutines.Dispatchers
import com.example.kotlinflows.foundation.BaseApplication
import com.example.kotlinflows.foundation.model.coroutines.IoDispatchers
import com.example.kotlinflows.foundation.model.coroutines.WorkDispatchers
import com.example.kotlinflows.simplemvvm.model.colors.InMemoryColorsRepository

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private val ioDispatchers = IoDispatchers(Dispatchers.IO)
    private val workDispatchers = WorkDispatchers(Dispatchers.Default)

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository(ioDispatchers)
    )
}