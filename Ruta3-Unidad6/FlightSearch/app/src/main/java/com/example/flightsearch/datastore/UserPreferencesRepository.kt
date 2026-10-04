package com.example.flightsearch.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "preferencias_usuario"
)

class UserPreferencesRepository(
    private val contexto: Context
) {

    private val claveBusqueda = stringPreferencesKey("texto_busqueda")

    val textoBusqueda: Flow<String> =
        contexto.dataStore.data.map { preferencias ->
            preferencias[claveBusqueda] ?: ""
        }

    suspend fun guardarTextoBusqueda(texto: String) {
        contexto.dataStore.edit { preferencias ->
            preferencias[claveBusqueda] = texto
        }
    }
}