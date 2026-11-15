package com.example.a23200581solis_24100586hancco_pc02.ui.asteroids

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.neo.AsteroidEntity
import com.example.a23200581solis_24100586hancco_pc02.data.repository.AsteroidRepository
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AsteroidsUiState(
    val items: List<AsteroidEntity> = emptyList(),
    val favorites: List<AsteroidEntity> = emptyList(),
    val onlyHazardous: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

private data class NeoLoadStatus(
    val loading: Boolean = false,
    val error: String? = null
)

class AsteroidsViewModel(
    private val repository: AsteroidRepository
) : ViewModel() {

    private val today = LocalDate.now()
    private val end = today.toString()
    private val start = today.minusDays(6).toString()

    private val filter = MutableStateFlow(false)
    private val status = MutableStateFlow(NeoLoadStatus())

    val uiState: StateFlow<AsteroidsUiState> = combine(
        repository.observeAll(start, end),
        repository.observeFavorites(),
        filter,
        status
    ) { items, favorites, onlyHazardous, load ->
        AsteroidsUiState(
            items = if (onlyHazardous) {
                items.filter { it.isPotentiallyHazardous }
            } else {
                items
            },
            favorites = favorites,
            onlyHazardous = onlyHazardous,
            isLoading = load.loading,
            error = load.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AsteroidsUiState()
    )

    init {
        refresh()
    }

    fun setOnlyHazardous(value: Boolean) {
        filter.value = value
    }

    fun observeDetail(id: String, date: String) =
        repository.observeDetail(id, date)

    fun refresh() {
        if (status.value.loading) return

        status.value = NeoLoadStatus(loading = true)

        viewModelScope.launch {
            try {
                repository.refresh(start, end)
                status.value = NeoLoadStatus()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                status.value = NeoLoadStatus(
                    error = "No se pudo actualizar NASA. " +
                            "Revisa la conexión y la clave."
                )
            }
        }
    }

    fun toggleFavorite(item: AsteroidEntity) {
        viewModelScope.launch {
            try {
                repository.setFavorite(
                    item.asteroidId,
                    !item.isFavorite
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                status.value = status.value.copy(
                    error = "No se pudo cambiar el favorito."
                )
            }
        }
    }

    class Factory(
        private val repository: AsteroidRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            require(
                modelClass.isAssignableFrom(
                    AsteroidsViewModel::class.java
                )
            )
            return AsteroidsViewModel(repository) as T
        }
    }
}