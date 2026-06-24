package com.sancarlina.app.viewmodel
 
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.repository.AreasRepository
import com.sancarlina.app.data.repository.TenantsRepository
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class TurismoDetailViewModel(
    private val tenantsRepository: TenantsRepository,
    private val areasRepository: AreasRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _point = MutableStateFlow<TurismoPoint?>(null)
    val point: StateFlow<TurismoPoint?> = _point.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPointDetails(pointId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val doc = withTimeoutOrNull(5000) {
                    firestore.collection("turismo_points").document(pointId).get().await()
                }
                if (doc != null && doc.exists()) {
                    val p = TurismoPoint(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        category = doc.getString("category") ?: "",
                        location = doc.getString("location") ?: "San Carlos Centro",
                        rating = doc.getDouble("rating") ?: 0.0,
                        phone = doc.getString("phone") ?: "",
                        schedule = doc.getString("schedule") ?: "",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0
                    )
                    _point.value = p
                } else {
                    _point.value = null
                }
            } catch (e: Exception) {
                Logger.e("Error loading turismo point details from Firestore", e)
                _point.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
