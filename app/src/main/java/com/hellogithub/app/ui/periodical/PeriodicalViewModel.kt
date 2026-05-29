package com.hellogithub.app.ui.periodical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.PeriodicalCategoryDto
import com.hellogithub.app.data.remote.dto.PeriodicalIssueDto
import com.hellogithub.app.data.repository.PeriodicalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PeriodicalUiState {
    data object Loading : PeriodicalUiState
    data class Success(
        val issues: List<PeriodicalIssueDto>,
        val selectedIssue: PeriodicalIssueDto?,
        val categories: List<PeriodicalCategoryDto>,
    ) : PeriodicalUiState
    data class Error(val message: String) : PeriodicalUiState
}

class PeriodicalViewModel(
    private val repository: PeriodicalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PeriodicalUiState>(PeriodicalUiState.Loading)
    val uiState: StateFlow<PeriodicalUiState> = _uiState.asStateFlow()

    init {
        loadIssueList()
    }

    private fun loadIssueList() {
        viewModelScope.launch {
            _uiState.value = PeriodicalUiState.Loading
            repository.getList().fold(
                onSuccess = { response ->
                    val issues = response.data
                    if (issues.isNotEmpty()) {
                        _uiState.value = PeriodicalUiState.Success(
                            issues = issues,
                            selectedIssue = null,
                            categories = emptyList(),
                        )
                        selectIssue(issues.first().volumeId)
                    } else {
                        _uiState.value = PeriodicalUiState.Error("暂无月刊数据")
                    }
                },
                onFailure = { e ->
                    _uiState.value = PeriodicalUiState.Error(e.message ?: "加载失败")
                },
            )
        }
    }

    fun selectIssue(volumeId: String) {
        viewModelScope.launch {
            val current = (_uiState.value as? PeriodicalUiState.Success) ?: return@launch
            repository.getDetail(volumeId).fold(
                onSuccess = { response ->
                    response.data?.let { detail ->
                        val issue = current.issues.find { it.volumeId == volumeId }
                        _uiState.update {
                            PeriodicalUiState.Success(
                                issues = current.issues,
                                selectedIssue = issue,
                                categories = detail.categories,
                            )
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update { PeriodicalUiState.Error(e.message ?: "加载失败") }
                },
            )
        }
    }
}
