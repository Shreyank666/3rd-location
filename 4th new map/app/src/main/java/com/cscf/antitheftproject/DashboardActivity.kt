package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        try {
            setupProfile()
            setupDashboardButtons()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error initializing dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupProfile() {
        val currentUser = auth.currentUser
        val userEmailView = findViewById<TextView>(R.id.tvUserEmail)
        val signOutButton = findViewById<Button>(R.id.btnSignOut)

        // Display user email
        userEmailView.text = currentUser?.email ?: "No email"

        // Handle sign out
        signOutButton.setOnClickListener {
            auth.signOut()
            // Stop location service
            stopService(Intent(this, LocationService::class.java))
            // Navigate back to login screen
            val intent = Intent(this, LoginRegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupDashboardButtons() {
        val findMyPhoneButton = findViewById<Button>(R.id.btnFindMyPhone)
        val remoteCommandsButton = findViewById<Button>(R.id.btnRemoteCommands)

        findMyPhoneButton.setOnClickListener {
            startActivity(Intent(this, FindMyPhoneOptionsActivity::class.java))
        }

        remoteCommandsButton.setOnClickListener {
            startActivity(Intent(this, RemoteCommandsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}