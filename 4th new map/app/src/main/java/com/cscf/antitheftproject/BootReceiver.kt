package com.cscf.antitheftproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Check if user is logged in
            if (FirebaseAuth.getInstance().currentUser != null) {
                val serviceIntent = Intent(context, LocationService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
} 