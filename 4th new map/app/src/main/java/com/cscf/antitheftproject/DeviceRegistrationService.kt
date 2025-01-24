package com.cscf.antitheftproject

import android.content.Context
import android.provider.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeviceRegistrationService(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerDevice() {
        val userId = auth.currentUser?.uid ?: return
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceInfo = hashMapOf(
            "deviceId" to deviceId,
            "model" to android.os.Build.MODEL,
            "manufacturer" to android.os.Build.MANUFACTURER,
            "registeredAt" to com.google.firebase.Timestamp.now(),
            "isActive" to true
        )

        db.collection("users")
            .document(userId)
            .collection("devices")
            .document(deviceId)
            .set(deviceInfo)
    }
} 