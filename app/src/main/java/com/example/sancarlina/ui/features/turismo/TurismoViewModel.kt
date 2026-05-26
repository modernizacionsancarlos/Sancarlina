package com.example.sancarlina.ui.features.turismo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TurismoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TurismoUiState())
    val uiState: StateFlow<TurismoUiState> = _uiState.asStateFlow()

    init {
        loadExperiences()
    }

    private fun loadExperiences() {
        _uiState.update { 
            it.copy(
                experiences = listOf(
                    ExperienceItem(
                        "1", 
                        "Atardecer en los Viñedos", 
                        "La Consulta", 
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDxrlCdWV-oLelj-JgQ0MijsJKJKVsP389UkDVE1BUSdv8p8PJjXpZInJvuNIDqNGGyPvYKHGFpQg0vo23GOxVWi2LL1gEBpApfvF-IA_KwNyKbZgrUXwDMN_xsj0QXVlIEuJ52VaIGlTrpvBV6fXC3GWvnl0bW-iXJWi1P6BX_CDhGES5l_7rPL5V-mCGLbgc9YR9xXI247rU99MMqEYY1UO6u4g8iFNTTwn3i8Iq-lsdUsGfpbxeVSVH0wkGR3Rq5Q0iLIa2KTOI",
                        "ENOTURISMO",
                        "Disfruta de una cata guiada mientras el sol se oculta tras la Cordillera de los Andes."
                    ),
                    ExperienceItem(
                        "2", 
                        "Ruta de la Miel", 
                        "Eugenio Bustos", 
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDR7fJTZFE8JC-jbBEEXqwKx4eQ9tKdpfStvBK4mTzoPzMk0SJns9EG6SD50Mpoz0XKOlTnz_LadQNg8nhvGMSodQbdkC-khhlnmMkF3u9kp6xdpW5HjOXWosku-khh2gMgGNb6yx_9GPoSx6beIl-Yro2pCcHCEJMlhC8lC8t-8ndz0YyOIGm3QQtk7lINPBoRkS7GdlzDmsbY9X-jeoGyk_tWogB_1aYLu-nrGO1KxCBm8HaaKV60PyGv5GuOh3RmDiS24P2qnyQ",
                        "ARTESANAL",
                        "Conoce el proceso de producción de la miel sancarlina desde la colmena hasta el frasco."
                    ),
                    ExperienceItem(
                        "3", 
                        "Senderismo Histórico", 
                        "Villa de San Carlos", 
                        "https://lh3.googleusercontent.com/aida-public/AB6AXuDw9EFruk8dtYQ3VOmMeX74BhP2e3DCbNJ2uQ2g4j2pqOv86Wl5qSie69ieAiZ--o0DQbu-aAm50puCwqXYNAb7Gv5YmlhZpO7hW-L-YqXlisMGIOaWRTEBY08t9jkI-L4dA6_3Hgo9JG2CQOHB-CPrV1voY2XADOsx6J936J44G3oRWAqCuuhLjriYjYswkEf5pWZiU_SMZFSanPOQNvYLyohZ5QcI-UO582RoDgOsn4eb2JtQIo-QLEilNaIWEyNht_INTw8D5Po",
                        "HISTORIA",
                        "Recorre los puntos fundacionales de nuestro departamento y descubre sus secretos."
                    )
                )
            )
        }
    }
}
