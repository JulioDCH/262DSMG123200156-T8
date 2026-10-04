package com.example.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.busschedule.data.BusSchedule
import com.example.busschedule.data.BusScheduleDatabase
import kotlinx.coroutines.flow.Flow

class BusScheduleViewModel(
    private val busScheduleDao: com.example.busschedule.data.BusScheduleDao
) : ViewModel() {

    fun getFullSchedule(): Flow<List<BusSchedule>> =
        busScheduleDao.getAll()

    fun getScheduleFor(stopName: String): Flow<List<BusSchedule>> =
        busScheduleDao.getByStopName(stopName)

    companion object {

        val factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {

                val application = requireNotNull(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                )

                BusScheduleViewModel(
                    BusScheduleDatabase
                        .getDatabase(application.applicationContext)
                        .busScheduleDao()
                )
            }
        }
    }
}