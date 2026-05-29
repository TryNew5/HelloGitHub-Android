package com.hellogithub.app.ui.periodical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.PeriodicalCategorySummaryDto
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
        val categories: List<PeriodicalCategorySummaryDto>,
        val repoTotal: Int = 0,
        val selectedIssue: PeriodicalIssueDto? = null,
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
                    val issues = response.volumes
                    if (issues.isNotEmpty()) {
                        _uiState.value = PeriodicalUiState.Success(
                            issues = issues,
                            categories = response.categories,
                            repoTotal = response.repoTotal,
                            selectedIssue = issues.firstOrNull(),
                        )
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

    fun selectIssue(issue: PeriodicalIssueDto) {
        _uiState.update { state ->
            if (state is PeriodicalUiState.Success) {
                state.copy(selectedIssue = issue)
            } else {
                state
            }
        }
    }

    fun getPeriodicalUrl(num: Int): String {
        return "https://hellogithub.com/periodical/volume/$num"
    }
}
