package com.cscf.antitheftproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class LocationTrackingActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST = 123
    private val TAG = "LocationTrackingActivity"
    private lateinit var currentLocationTextView: TextView
    private lateinit var timestampTextView: TextView
    private lateinit var coordinatesTextView: TextView
    private lateinit var map: MapView
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Osmdroid configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        
        setContentView(R.layout.activity_location_tracking)

        setupViews()
        setupMap()
        checkAndRequestPermissions()
    }

    private fun setupViews() {
        currentLocationTextView = findViewById(R.id.tvLocation)
        timestampTextView = findViewById(R.id.tvTimestamp)
        coordinatesTextView = findViewById(R.id.tvCoordinates)
        map = findViewById(R.id.map)

        findViewById<Button>(R.id.btnStopTracking).setOnClickListener {
            stopLocationService()
            finish()
        }
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)
    }

    private fun setupLocationTracking() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        
        db.collection("locations")
            .document(userId)
            .collection("current")
            .document("latest")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Failed to read current location", error)
                    Toast.makeText(this, "Failed to read location: ${error.message}", 
                        Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val locationData = it.toObject(LocationData::class.java)
                    locationData?.let { data ->
                        updateLocationUI(data)
                        updateMapLocation(data)
                    }
                }
            }
    }

    private fun updateLocationUI(locationData: LocationData) {
        val formattedLatitude = String.format("%.6f", locationData.latitude)
        val formattedLongitude = String.format("%.6f", locationData.longitude)
        
        coordinatesTextView.text = "📍 $formattedLatitude, $formattedLongitude"
        currentLocationTextView.text = "Accuracy: ${locationData.accuracy} meters"

        val date = locationData.timestamp.toDate()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        timestampTextView.text = "Last Updated: ${format.format(date)}"
    }

    private fun updateMapLocation(locationData: LocationData) {
        val location = GeoPoint(locationData.latitude, locationData.longitude)
        
        currentMarker?.let { map.overlays.remove(it) }
        
        currentMarker = Marker(map).apply {
            position = location
            title = "Current Location"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        
        map.overlays.add(currentMarker)
        map.controller.animateTo(location)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                notGrantedPermissions.toTypedArray(),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            startLocationService()
        }
    }

    private fun startLocationService() {
        Log.d(TAG, "Starting location service")
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopLocationService() {
        stopService(Intent(this, LocationService::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startLocationService()
            } else {
                Toast.makeText(this, 
                    "Location permissions are required for tracking", 
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the location service when activity is destroyed
        stopLocationService()
    }
}