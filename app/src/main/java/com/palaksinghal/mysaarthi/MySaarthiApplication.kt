package com.palaksinghal.mysaarthi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.palaksinghal.mysaarthi.domain.repository.LocationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MySaarthiApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // Fires exactly once whenever the app process comes to the
                // foreground — not on screen rotation, not on internal navigation

                android.util.Log.d("LocationUpdate", "App came to foreground — checking location")

                val locationRepository = EntryPointAccessors.fromApplication(
                    applicationContext,
                    LocationRepositoryEntryPoint::class.java
                ).locationRepository()

                CoroutineScope(Dispatchers.IO).launch {
                    locationRepository.updateUserLocation()
                        .onSuccess {
                            android.util.Log.d("LocationUpdate", "Location updated successfully")
                        }
                        .onFailure { throwable ->
                            android.util.Log.e("LocationUpdate", "Location update failed: ${throwable.message}", throwable)
                        }
                }
            }
        })
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LocationRepositoryEntryPoint {
    fun locationRepository(): LocationRepository
}