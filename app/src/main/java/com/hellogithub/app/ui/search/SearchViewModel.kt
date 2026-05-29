package com.hellogithub.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.HomeItemDto
import com.hellogithub.app.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Searching : SearchUiState
    data class Success(val results: List<HomeItemDto>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel(
    private val repository: SearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        searchJob = null

        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)

            // Guard: if this coroutine was cancelled during delay, stop here.
            // CancellationException from delay() will propagate naturally
            // because safeApiCall doesn't swallow it.

            _uiState.value = SearchUiState.Searching

            repository.search(newQuery).fold(
                onSuccess = { response ->
                    // Double-check we're still the active search
                    _uiState.update {
                        if (response.data.isEmpty()) SearchUiState.Empty
                        else SearchUiState.Success(response.data)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        SearchUiState.Error(e.message ?: "搜索失败")
                    }
                },
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
