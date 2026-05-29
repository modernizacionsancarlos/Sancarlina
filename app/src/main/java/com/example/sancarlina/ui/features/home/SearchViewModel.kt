package com.example.sancarlina.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = ""
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

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        if (newQuery.length >= 3) {
            performSearch(newQuery)
        } else {
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    private fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true) }
        
        // Search in "products" and "commerces"
        // Note: Firestore doesn't support full-text search easily without Algolia, 
        // but we can simulate it with prefix matching for small datasets.
        
        val results = mutableListOf<SearchResult>()
        
        firestore.collection("products")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .get()
            .addOnSuccessListener { productDocs ->
                productDocs.documents.forEach { doc ->
                    results.add(SearchResult(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "",
                        type = "PRODUCT"
                    ))
                }
                
                // Then search commerces
                firestore.collection("commerces")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get()
                    .addOnSuccessListener { commerceDocs ->
                        commerceDocs.documents.forEach { doc ->
                            results.add(SearchResult(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                category = doc.getString("category") ?: "",
                                type = "COMMERCE"
                            ))
                        }
                        _uiState.update { it.copy(results = results, isLoading = false) }
                    }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false) }
            }
    }
}
