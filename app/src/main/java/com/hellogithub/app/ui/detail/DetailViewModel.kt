package com.hellogithub.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.RepositoryDto
import com.hellogithub.app.data.repository.RepoDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val repo: RepositoryDto) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: RepoDetailRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetail(rid: String) {
        viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }
            repository.getDetail(rid).fold(
                onSuccess = { response ->
                    response.data?.let { repo ->
                        _uiState.update { DetailUiState.Success(repo) }
                    } ?: run {
                        _uiState.update { DetailUiState.Error("项目不存在") }
                    }
                },
                onFailure = { e ->
                    _uiState.update { DetailUiState.Error(e.message ?: "加载失败") }
                },
            )
        }
    }
}
