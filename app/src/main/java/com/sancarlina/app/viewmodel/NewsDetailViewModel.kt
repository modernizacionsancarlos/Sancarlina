package com.sancarlina.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class NewsDetailViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    
    private val _newsItem = MutableStateFlow<BannerItem?>(null)
    val newsItem: StateFlow<BannerItem?> = _newsItem.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadNewsDetails(newsId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val doc = withTimeoutOrNull(5000) {
                    firestore.collection("banners").document(newsId).get().await()
                }
                if (doc != null && doc.exists()) {
                    val item = BannerItem(
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
                    _newsItem.value = item
                } else {
                    _newsItem.value = null
                }
            } catch (e: Exception) {
                Logger.e("Error loading news details from Firestore", e)
                _newsItem.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
