package com.example.a23200581solis_24100586hancco_pc02.ui.images

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.ApodEntity
import com.example.a23200581solis_24100586hancco_pc02.data.repository.ApodRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ImagesUiState(
    val images: List<ApodEntity> = emptyList(),
    val favorites: List<ApodEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

private data class LoadStatus(
    val isLoading: Boolean = false,
    val error: String? = null
)

class ImagesViewModel(
    private val repository: ApodRepository
) : ViewModel() {

    private val dates = ApodRepository.lastSevenDays()
    private val loadStatus = MutableStateFlow(LoadStatus())

    val uiState: StateFlow<ImagesUiState> = combine(
        repository.observeImages(dates.first, dates.second),
        repository.observeFavorites(),
        loadStatus
    ) { images, favorites, status ->
        ImagesUiState(
            images = images,
            favorites = favorites,
            isLoading = status.isLoading,
            error = status.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ImagesUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        if (loadStatus.value.isLoading) return

        loadStatus.value = LoadStatus(isLoading = true)

        viewModelScope.launch {
            try {
                repository.refreshImages(
                    startDate = dates.first,
                    endDate = dates.second
                )
                loadStatus.value = LoadStatus()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                loadStatus.value = LoadStatus(
                    error = "No se pudo actualizar NASA. " +
                            "Revisa tu conexión y la clave configurada."
                )
            }
        }
    }

    fun observeImage(date: String): Flow<ApodEntity?> {
        return repository.observeImageByDate(date)
    }

    fun toggleFavorite(image: ApodEntity) {
        viewModelScope.launch {
            try {
                repository.setFavorite(
                    date = image.date,
                    isFavorite = !image.isFavorite
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                loadStatus.value = loadStatus.value.copy(
                    error = "No se pudo cambiar el favorito."
                )
            }
        }
    }

    class Factory(
        private val repository: ApodRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            require(
                modelClass.isAssignableFrom(ImagesViewModel::class.java)
            ) {
                "ViewModel desconocido"
            }

            return ImagesViewModel(repository) as T
        }
    }
}