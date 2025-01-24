package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        try {
            val findMyPhoneButton = findViewById<Button>(R.id.btnFindMyPhone)
            val remoteCommandsButton = findViewById<Button>(R.id.btnRemoteCommands)
            val securityAlertsButton = findViewById<Button>(R.id.btnSecurityAlerts)

            findMyPhoneButton.setOnClickListener {
                // Navigate to LocationTrackingActivity
                startActivity(Intent(this, LocationTrackingActivity::class.java))
            }

            remoteCommandsButton.setOnClickListener {
                // Navigate to RemoteCommandsActivity
                startActivity(Intent(this, RemoteCommandsActivity::class.java))
            }

            securityAlertsButton.setOnClickListener {
                // Implement security alerts functionality
                Toast.makeText(this, "Security Alerts coming soon!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error initializing dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        // Prevent going back to login screen
        moveTaskToBack(true)
    }
}