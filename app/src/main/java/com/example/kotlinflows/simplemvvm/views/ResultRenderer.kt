package com.example.kotlinflows.simplemvvm.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import com.example.kotlinflows.foundation.model.ErrorResult
import com.example.kotlinflows.foundation.model.PendingResult
import com.example.kotlinflows.foundation.model.Result
import com.example.kotlinflows.foundation.model.SuccessResult
import com.example.kotlinflows.foundation.views.BaseFragment
import com.example.kotlinflows.simplemvvm.R
import com.example.kotlinflows.simplemvvm.databinding.PartResultBinding

/**
 * Default [Result] rendering.
 * - if [result] is [PendingResult] -> only progress-bar is displayed
 * - if [result] is [ErrorResult] -> only error container is displayed
 * - if [result] is [SuccessResult] -> error container & progress-bar is hidden, all other views are visible
 */
fun <T> BaseFragment.renderSimpleResult(
    root: ViewGroup,
    result: Result<T>,
    onSuccess: (T) -> Unit
) {
    val binding = PartResultBinding.bind(root)

    renderResult(
        root = root,
        result = result,
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onSuccess = { successData ->
            root.children
                .filter { it.id != R.id.progressBar && it.id != R.id.errorContainer }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        }
    )
}

/**
 * Assign onClick listener for default try-again button.
 */
fun onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.tryAgainButton).setOnClickListener { onTryAgainPressed() }
}