package com.cscf.antitheftproject

import com.google.firebase.Timestamp

data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now(),
    val accuracy: Float = 0f,
    val provider: String = "",
    val speed: Float = 0f,
    val altitude: Double = 0.0
)