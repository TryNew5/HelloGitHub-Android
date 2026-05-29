package com.hellogithub.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellogithub.app.data.remote.dto.HomeItemDto
import com.hellogithub.app.data.remote.dto.TagDto
import com.hellogithub.app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val items: List<HomeItemDto> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = false,
    val error: String? = null,
    val selectedTopicId: String = "all",
    val selectedSort: String = "featured",
    val selectedRank: String? = null,
)

class FeedViewModel(
    private val repository: HomeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadTags()
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.items.isEmpty(), error = null) }
            val state = _uiState.value
            repository.getFeed(
                sortBy = state.selectedSort,
                rankBy = state.selectedRank,
                topicId = state.selectedTopicId.takeIf { it != "all" },
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            items = response.data,
                            hasMore = response.hasMore,
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
            loadFeed()
        }
    }

    fun selectTopic(topicId: String) {
        _uiState.update { it.copy(selectedTopicId = topicId) }
        loadFeed()
    }

    fun selectSort(sort: String) {
        _uiState.update { it.copy(selectedSort = sort) }
        loadFeed()
    }

    fun selectRank(rank: String?) {
        _uiState.update { it.copy(selectedRank = rank) }
        loadFeed()
    }

    private fun loadTags() {
        viewModelScope.launch {
            repository.getTags().fold(
                onSuccess = { response ->
                    _uiState.update { it.copy(tags = response.data) }
                },
                onFailure = { /* silent */ },
            )
        }
    }
}
