package com.example.kotlinflows.simplemvvm.views.changecolor

import androidx.lifecycle.*
import androidx.lifecycle.map
import com.example.kotlinflows.foundation.model.*
import com.example.kotlinflows.foundation.sideeffects.navigator.Navigator
import com.example.kotlinflows.foundation.sideeffects.resources.Resources
import com.example.kotlinflows.foundation.sideeffects.toasts.Toasts
import com.example.kotlinflows.foundation.views.BaseViewModel
import com.example.kotlinflows.foundation.views.LiveResult
import com.example.kotlinflows.foundation.views.MediatorLiveResult
import com.example.kotlinflows.foundation.views.MutableLiveResult
import com.example.kotlinflows.simplemvvm.R
import com.example.kotlinflows.simplemvvm.model.colors.ColorsRepository
import com.example.kotlinflows.simplemvvm.model.colors.NamedColor
import com.example.kotlinflows.simplemvvm.views.changecolor.ChangeColorFragment.Screen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.Result

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getStateFlow("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    // main destination (contains merged values from _availableColors & _currentColorId)
    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _saveInProgress,
        ::mergeSources
    )

    val screenTitle: LiveData<String> = viewState.map { result ->
        return@map if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.change_color_screen_title_simple)
        }
    }.asLiveData()

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _saveInProgress.value = true
            val currentColorId =
                _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)
            colorsRepository.setCurrentColor(currentColor).collect()
            navigator.goBack(currentColor)
        } catch (e: Exception) {
            if (e is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _saveInProgress.value = false
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data) + whether save is in progress or not, then we use all of
     * these values in order to create a [ViewState] instance, which is in its turn rendered by fragment.
     */
    private fun mergeSources(
        colors: Result<List<NamedColor>>,
        currentColorId: Long,
        saveInProgress: Progress
    ): Result<ViewState> {

        // map Result<List<NamedColor>> to Result<ViewState>
        return colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },

                showSaveButton = !saveInProgress.isInProgress(),
                showCancelButton = !saveInProgress.isInProgress(),
                showSaveProgressBar = saveInProgress.isInProgress(),

                saveProgressPercentage = saveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value)
            )
        }
    }

    private fun load() = into(_availableColors) {
        colorsRepository.getAvailableColors()
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String,
    )
}