package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {

    @Query("""
        SELECT * FROM airport
        ORDER BY passengers DESC
    """)
    fun obtenerTodosLosAeropuertos(): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport
        WHERE iata_code = :iataCode
        LIMIT 1
    """)
    suspend fun obtenerAeropuertoPorIata(iataCode: String): Airport?

    @Query("""
        SELECT * FROM airport
        WHERE name LIKE '%' || :texto || '%'
        OR iata_code LIKE '%' || :texto || '%'
        ORDER BY passengers DESC
    """)
    fun buscarAeropuertos(texto: String): Flow<List<Airport>>

    @Query("""
        SELECT * FROM airport
        WHERE iata_code != :codigoSalida
        ORDER BY passengers DESC
    """)
    fun obtenerDestinos(codigoSalida: String): Flow<List<Airport>>

    @Query("""
        SELECT * FROM favorite
    """)
    fun obtenerFavoritos(): Flow<List<Favorite>>

    @Query("""
        SELECT * FROM favorite
        WHERE departure_code = :departureCode AND destination_code = :destinationCode
        LIMIT 1
    """)
    suspend fun obtenerFavorito(departureCode: String, destinationCode: String): Favorite?

    @Insert
    suspend fun insertarFavorito(favorito: Favorite)

    @Delete
    suspend fun eliminarFavorito(favorito: Favorite)
}