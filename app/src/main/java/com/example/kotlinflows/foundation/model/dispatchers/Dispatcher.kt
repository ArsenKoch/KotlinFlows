package com.example.kotlinflows.foundation.model.dispatchers

/**
 * Dispatchers run the specified block of code in some way.
 */
interface Dispatcher {

    fun dispatch(block: () -> Unit)
}