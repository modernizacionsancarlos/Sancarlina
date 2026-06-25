package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class NewsListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(NewsListUiState(isLoading = true))
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    private fun loadNews() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val snapshot = withTimeoutOrNull(5000) {
                    firestore.collection("banners").get().await()
                }

                val items = if (snapshot != null && !snapshot.isEmpty) {
                    snapshot.documents.map { doc ->
                        BannerItem(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            content = doc.getString("content") ?: "",
                            date = doc.getString("date") ?: "",
                            tag = doc.getString("tag") ?: "",
                            authorName = doc.getString("authorName") ?: "",
                            authorRole = doc.getString("authorRole") ?: "",
                            authorImageUrl = doc.getString("authorImageUrl") ?: "",
                            readingTime = doc.getString("readingTime") ?: "3 min de lectura"
                        )
                    }
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(news = items, isLoading = false)
                }
            } catch (e: Exception) {
                Logger.e("Error loading news from Firestore", e)
                _uiState.update {
                    it.copy(news = emptyList(), isLoading = false)
                }
            }
        }
    }
}

data class NewsListUiState(
    val news: List<BannerItem> = emptyList(),
    val isLoading: Boolean = false
)
