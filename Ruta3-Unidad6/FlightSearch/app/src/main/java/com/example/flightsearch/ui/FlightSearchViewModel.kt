package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.Favorite
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.datastore.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewModel(
    private val repositorio: FlightRepository,
    private val preferencias: UserPreferencesRepository
) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _aeropuertoSeleccionado = MutableStateFlow<Airport?>(null)
    val aeropuertoSeleccionado: StateFlow<Airport?> = _aeropuertoSeleccionado.asStateFlow()

    // 1. Evitamos lanzar múltiples collect utilizando flatMapLatest y stateIn
    val aeropuertos: StateFlow<List<Airport>> = _textoBusqueda
        .flatMapLatest { texto ->
            // Si el texto está en blanco o ya se seleccionó un aeropuerto, no mostramos sugerencias
            if (texto.isBlank() || _aeropuertoSeleccionado.value != null) {
                flowOf(emptyList())
            } else {
                repositorio.buscarAeropuertos(texto)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val destinos: StateFlow<List<Airport>> = _aeropuertoSeleccionado
        .flatMapLatest { aeropuerto ->
            if (aeropuerto == null) {
                flowOf(emptyList())
            } else {
                repositorio.obtenerDestinos(aeropuerto.iata_code)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoritos: StateFlow<List<Favorite>> = repositorio.obtenerFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Mapa de aeropuertos para resolver nombres completos en los favoritos
    val mapaAeropuertos: StateFlow<Map<String, Airport>> = repositorio.obtenerTodosLosAeropuertos()
        .map { lista -> lista.associateBy { it.iata_code } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        recuperarBusqueda()
    }

    fun cambiarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto
        _aeropuertoSeleccionado.value = null // Al modificar la búsqueda se deselecciona el aeropuerto actual

        viewModelScope.launch {
            preferencias.guardarTextoBusqueda(texto)
        }
    }

    fun limpiarBusqueda() {
        _textoBusqueda.value = ""
        _aeropuertoSeleccionado.value = null

        viewModelScope.launch {
            preferencias.guardarTextoBusqueda("")
        }
    }

    fun seleccionarAeropuerto(aeropuerto: Airport) {
        _aeropuertoSeleccionado.value = aeropuerto
        _textoBusqueda.value = aeropuerto.iata_code

        viewModelScope.launch {
            preferencias.guardarTextoBusqueda(aeropuerto.iata_code)
        }
    }

    fun toggleFavorito(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            val favoritoExistente = repositorio.obtenerFavorito(departureCode, destinationCode)
            if (favoritoExistente != null) {
                repositorio.eliminarFavorito(favoritoExistente)
            } else {
                repositorio.insertarFavorito(
                    Favorite(
                        departure_code = departureCode,
                        destination_code = destinationCode
                    )
                )
            }
        }
    }

    fun eliminarFavorito(favorito: Favorite) {
        viewModelScope.launch {
            repositorio.eliminarFavorito(favorito)
        }
    }

    private fun recuperarBusqueda() {
        viewModelScope.launch {
            val textoGuardado = preferencias.textoBusqueda.first()
            if (textoGuardado.isNotBlank()) {
                _textoBusqueda.value = textoGuardado
                val coincidencia = repositorio.obtenerAeropuertoPorIata(textoGuardado)
                if (coincidencia != null) {
                    _aeropuertoSeleccionado.value = coincidencia
                }
            }
        }
    }
}

class FlightSearchViewModelFactory(
    private val repositorio: FlightRepository,
    private val preferencias: UserPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightSearchViewModel(
                repositorio,
                preferencias
            ) as T
        }

        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}