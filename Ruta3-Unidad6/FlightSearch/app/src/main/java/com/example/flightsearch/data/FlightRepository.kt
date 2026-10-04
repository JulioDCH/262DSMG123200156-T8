package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class FlightRepository(
    private val flightDao: FlightDao
) {

    fun obtenerTodosLosAeropuertos(): Flow<List<Airport>> {
        return flightDao.obtenerTodosLosAeropuertos()
    }

    suspend fun obtenerAeropuertoPorIata(iataCode: String): Airport? {
        return flightDao.obtenerAeropuertoPorIata(iataCode)
    }

    fun buscarAeropuertos(texto: String): Flow<List<Airport>> {
        return flightDao.buscarAeropuertos(texto)
    }

    fun obtenerDestinos(codigoSalida: String): Flow<List<Airport>> {
        return flightDao.obtenerDestinos(codigoSalida)
    }

    fun obtenerFavoritos(): Flow<List<Favorite>> {
        return flightDao.obtenerFavoritos()
    }

    suspend fun obtenerFavorito(departureCode: String, destinationCode: String): Favorite? {
        return flightDao.obtenerFavorito(departureCode, destinationCode)
    }

    suspend fun insertarFavorito(favorito: Favorite) {
        flightDao.insertarFavorito(favorito)
    }

    suspend fun eliminarFavorito(favorito: Favorite) {
        flightDao.eliminarFavorito(favorito)
    }
}