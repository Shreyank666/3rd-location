package com.cscf.antitheftproject

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val CHANNEL_ID = "LocationServiceChannel"
    private val NOTIFICATION_ID = 1
    private val TAG = "LocationService"

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var lastLocationTime: Long = 0
    private val MIN_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(5) // 5 minutes

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        requestLocationUpdates()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Tracking Active")
            .setContentText("Monitoring device location")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastLocationTime >= MIN_UPDATE_INTERVAL) {
                        saveLocation(location)
                        lastLocationTime = currentTime
                    }
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                TimeUnit.MINUTES.toMillis(5)
            ).apply {
                setMinUpdateDistanceMeters(50f)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Error requesting location updates", e)
        }
    }

    private fun saveLocation(location: Location) {
        val userId = auth.currentUser?.uid ?: return
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "accuracy" to location.accuracy,
            "timestamp" to Timestamp.now(),
            "provider" to location.provider,
            "speed" to location.speed,
            "altitude" to location.altitude
        )

        // Save current location
        db.collection("locations")
            .document(userId)
            .collection("current")
            .document("latest")
            .set(locationData)
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving current location", e)
            }

        // Save to history
        db.collection("locations")
            .document(userId)
            .collection("history")
            .add(locationData)
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving location history", e)
            }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
} 