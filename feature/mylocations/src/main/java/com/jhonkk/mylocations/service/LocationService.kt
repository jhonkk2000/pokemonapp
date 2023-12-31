package com.jhonkk.mylocations.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jhonkk.common.model.MyLocation
import com.jhonkk.data.repository.LocationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationService: Service() {

    @Inject
    lateinit var locationClient: LocationClient
    @Inject
    lateinit var repository: LocationRepository
    private var serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.cancel()
        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        when (intent?.action) {
            ACTION_START, ACTION_START_IN_BACKGROUND -> start(intent?.action?: "")
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(action: String) {
        STARTED = true
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                "location",
                "MyLocation",
                NotificationManager.IMPORTANCE_DEFAULT
            ))
        }
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("MyLocations")
            .setContentText("Obteniendo ubicación actual")
            .setSmallIcon(androidx.core.R.drawable.ic_call_decline)
            .setOngoing(true)

        locationClient.getLocationUpdates(60000L * 2)
            .catch { e ->
                e.printStackTrace() }
            .onEach { location ->
                repository.insertNetworkLocation(
                    MyLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                ).catch {  }.collect { }
            }
            .launchIn(serviceScope)

        if (action == ACTION_START_IN_BACKGROUND) {
            startForeground(1, notification.build())
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_START_IN_BACKGROUND = "ACTION_START_IN_BACKGROUND"
        const val ACTION_STOP = "ACTION_STOP"
        var STARTED = false
    }
}