package com.example.flightsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.FlightDatabase
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.datastore.UserPreferencesRepository
import com.example.flightsearch.ui.FlightSearchScreen
import com.example.flightsearch.ui.FlightSearchViewModel
import com.example.flightsearch.ui.FlightSearchViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val baseDatos = FlightDatabase.obtenerBaseDatos(applicationContext)

        val repositorio = FlightRepository(
            baseDatos.flightDao()
        )

        val preferencias = UserPreferencesRepository(
            applicationContext
        )

        setContent {

            val vistaModelo: FlightSearchViewModel = viewModel(
                factory = FlightSearchViewModelFactory(
                    repositorio,
                    preferencias
                )
            )

            FlightSearchScreen(
                vistaModelo = vistaModelo
            )
        }
    }
}