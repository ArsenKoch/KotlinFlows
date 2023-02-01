package com.example.kotlinflows.simplemvvm

import android.app.Application
import com.example.kotlinflows.foundation.BaseApplication
import com.example.kotlinflows.foundation.model.coroutines.IoDispatchers
import com.example.kotlinflows.simplemvvm.model.colors.InMemoryColorsRepository
import kotlinx.coroutines.Dispatchers

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private val ioDispatchers = IoDispatchers(Dispatchers.IO)

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository(ioDispatchers)
    )
}