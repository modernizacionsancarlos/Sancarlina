package com.sancarlina.app.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.data.repository.TenantsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class SearchViewModel(
    private val tenantsRepository: TenantsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null
    private var activeTenants: List<Tenant> = emptyList()

    init {
        viewModelScope.launch {
            tenantsRepository.observeActiveTenants().collect { tenants ->
                activeTenants = tenants
                val currentQuery = _uiState.value.query.trim()
                if (currentQuery.length >= 3) performSearch(currentQuery)
            }
        }
    }

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
        val term = query.trim()
        val results = activeTenants.asSequence()
            .filter { tenant ->
                tenant.name.contains(term, ignoreCase = true) ||
                    tenant.industry.contains(term, ignoreCase = true) ||
                    tenant.description.contains(term, ignoreCase = true) ||
                    tenant.address.contains(term, ignoreCase = true)
            }
            .sortedBy { it.name.lowercase() }
            .map { tenant ->
                SearchResult(
                    id = tenant.id,
                    name = tenant.name,
                    category = tenant.industry,
                    type = "COMMERCE"
                )
            }
            .toList()
        _uiState.update { it.copy(results = results, isLoading = false, error = null) }
    }
}
