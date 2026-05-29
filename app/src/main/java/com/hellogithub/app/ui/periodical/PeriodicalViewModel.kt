package com.hellogithub.app.ui.periodical

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.PeriodicalCategoryDto
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
    data class VolumeList(
        val issues: List<PeriodicalIssueDto>,
        val categories: List<PeriodicalCategorySummaryDto>,
        val repoTotal: Int = 0,
        val selectedIssueNum: Int? = null,
    ) : PeriodicalUiState
    data class VolumeDetail(
        val issues: List<PeriodicalIssueDto>,
        val selectedIssueNum: Int,
        val categories: List<PeriodicalCategoryDto>,
        val publishAt: String = "",
        val repoTotal: Int = 0,
    ) : PeriodicalUiState
    data class Error(val message: String) : PeriodicalUiState
}

class PeriodicalViewModel(
    private val repository: PeriodicalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PeriodicalUiState>(PeriodicalUiState.Loading)
    val uiState: StateFlow<PeriodicalUiState> = _uiState.asStateFlow()

    private var allIssues: List<PeriodicalIssueDto> = emptyList()
    private var summaryCategories: List<PeriodicalCategorySummaryDto> = emptyList()
    private var repoTotal: Int = 0

    init {
        loadIssueList()
    }

    private fun loadIssueList() {
        viewModelScope.launch {
            _uiState.value = PeriodicalUiState.Loading
            repository.getList().fold(
                onSuccess = { response ->
                    allIssues = response.volumes
                    summaryCategories = response.categories
                    repoTotal = response.repoTotal
                    val firstIssue = allIssues.firstOrNull()
                    if (firstIssue != null) {
                        _uiState.value = PeriodicalUiState.VolumeList(
                            issues = allIssues,
                            categories = summaryCategories,
                            repoTotal = repoTotal,
                            selectedIssueNum = firstIssue.num,
                        )
                        loadVolume(firstIssue.num)
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

    fun selectIssue(num: Int) {
        _uiState.update { state ->
            if (state is PeriodicalUiState.VolumeList) {
                state.copy(selectedIssueNum = num)
            } else if (state is PeriodicalUiState.VolumeDetail) {
                state.copy(selectedIssueNum = num)
            } else state
        }
        loadVolume(num)
    }

    private fun loadVolume(num: Int) {
        viewModelScope.launch {
            // Show loading while keeping the list visible
            _uiState.update { current ->
                when (current) {
                    is PeriodicalUiState.VolumeList -> current.copy(selectedIssueNum = num)
                    is PeriodicalUiState.VolumeDetail -> current.copy(
                        selectedIssueNum = num,
                        categories = emptyList()
                    )
                    else -> current
                }
            }
            repository.getVolume(num).fold(
                onSuccess = { volume ->
                    _uiState.value = PeriodicalUiState.VolumeDetail(
                        issues = allIssues,
                        selectedIssueNum = num,
                        categories = volume.data,
                        publishAt = volume.publishAt,
                        repoTotal = repoTotal,
                    )
                },
                onFailure = { e ->
                    // Keep current state but could show a snackbar
                    if (_uiState.value !is PeriodicalUiState.VolumeDetail) {
                        _uiState.value = PeriodicalUiState.Error(e.message ?: "加载月刊详情失败")
                    }
                },
            )
        }
    }
}
