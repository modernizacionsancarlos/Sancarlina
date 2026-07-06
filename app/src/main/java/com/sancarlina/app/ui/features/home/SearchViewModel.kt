package com.sancarlina.app.ui.features.home

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

data class SearchUiState(
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val error: String? = null
)

data class SearchResult(
    val id: String,
    val name: String,
    val category: String,
    val type: String // "PRODUCT" or "COMMERCE"
)

class SearchViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, error = null) }
        
        searchJob?.cancel()
        
        val trimmedQuery = newQuery.trim()
        if (trimmedQuery.length >= 3) {
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(500)
                val sanitizedQuery = com.sancarlina.app.utils.InputValidator.sanitizeText(trimmedQuery, 40)
                if (sanitizedQuery.length >= 3) {
                    performSearch(sanitizedQuery)
                } else {
                    _uiState.update { it.copy(results = emptyList()) }
                }
            }
        } else {
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    private fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                val results = mutableListOf<SearchResult>()
                
                // 1. Search products
                val productSnapshot = firestore.collection("products")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get()
                    .await()
                
                productSnapshot.documents.forEach { doc ->
                    results.add(SearchResult(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        type = "PRODUCT"
                    ))
                }
                
                // 2. Search commerces
                val commerceSnapshot = firestore.collection("commerces")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get()
                    .await()
                
                commerceSnapshot.documents.forEach { doc ->
                    results.add(SearchResult(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        type = "COMMERCE"
                    ))
                }
                
                _uiState.update { it.copy(results = results, isLoading = false) }
            } catch (e: Exception) {
                Logger.e("Search failed", e)
                _uiState.update { it.copy(isLoading = false, error = "No se pudo completar la búsqueda") }
            }
        }
    }
}
