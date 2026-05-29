package com.hellogithub.app.ui.rank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.HomeItemDto
import com.hellogithub.app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class YearMonth(val year: Int, val month: Int) {
    val label: String get() = "${year}年${month}月"
}

data class RankUiState(
    val items: List<HomeItemDto> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedPeriod: String = "monthly",
    val selectedYear: Int? = null,        // used for yearly
    val selectedYearMonth: YearMonth? = null,  // used for monthly
)

class RankViewModel(
    private val repository: HomeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankUiState())
    val uiState: StateFlow<RankUiState> = _uiState.asStateFlow()

    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH) + 1  // Calendar.MONTH is 0-based

    init {
        loadRanking()
    }

    fun loadRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.items.isEmpty(), error = null) }
            val state = _uiState.value

            val yearParam: Int?
            val monthParam: Int?
            if (state.selectedPeriod == "monthly" && state.selectedYearMonth != null) {
                yearParam = state.selectedYearMonth!!.year
                monthParam = state.selectedYearMonth!!.month
            } else {
                yearParam = state.selectedYear
                monthParam = null
            }

            repository.getFeed(
                rankBy = state.selectedPeriod,
                year = yearParam,
                month = monthParam,
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            items = response.data,
                            isLoading = false,
                            isRefreshing = false,
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = e.message ?: "加载失败",
                        )
                    }
                },
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            loadRanking()
        }
    }

    fun selectPeriod(period: String) {
        when (period) {
            "weekly" -> _uiState.update {
                it.copy(selectedPeriod = period, selectedYear = null, selectedYearMonth = null)
            }
            "monthly" -> _uiState.update {
                it.copy(
                    selectedPeriod = period,
                    selectedYear = null,
                    selectedYearMonth = YearMonth(currentYear, currentMonth),
                )
            }
            "yearly" -> _uiState.update {
                it.copy(selectedPeriod = period, selectedYear = currentYear, selectedYearMonth = null)
            }
        }
        loadRanking()
    }

    fun selectYear(year: Int?) {
        _uiState.update { it.copy(selectedYear = year) }
        loadRanking()
    }

    fun selectYearMonth(ym: YearMonth?) {
        _uiState.update { it.copy(selectedYearMonth = ym) }
        loadRanking()
    }

    fun getAvailableYears(): List<Int> {
        return (currentYear downTo currentYear - 5).toList()
    }

    /** Recent 12 months, newest first. */
    fun getRecentMonths(): List<YearMonth> {
        val months = mutableListOf<YearMonth>()
        var y = currentYear
        var m = currentMonth
        for (i in 1..12) {
            months.add(YearMonth(y, m))
            m -= 1
            if (m < 1) {
                m = 12
                y -= 1
            }
        }
        return months
    }
}
