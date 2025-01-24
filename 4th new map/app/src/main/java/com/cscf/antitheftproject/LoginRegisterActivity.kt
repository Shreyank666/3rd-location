package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.cscf.antitheftproject.DashboardActivity
import androidx.core.content.ContextCompat

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true
    private val TAG = "LoginRegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.etEmail)
        val passwordField = findViewById<EditText>(R.id.etPassword)
        val actionButton = findViewById<Button>(R.id.btnAction)
        val toggleText = findViewById<TextView>(R.id.tvToggle)

        actionButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (validateForm(email, password)) {
                if (isLoginMode) {
                    loginUser(email, password)
                } else {
                    registerUser(email, password)
                }
            }
        }

        toggleText.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        updateUI()
    }

    private fun updateUI() {
        val actionButton = findViewById<Button>(R.id.btnAction)
        val toggleText = findViewById<TextView>(R.id.tvToggle)

        if (isLoginMode) {
            actionButton.text = "Login"
            toggleText.text = "Switch to Register"
        } else {
            actionButton.text = "Register"
            toggleText.text = "Switch to Login"
        }
    }

    private fun onAuthSuccess() {
        val deviceRegistrationService = DeviceRegistrationService(this)
        deviceRegistrationService.registerDevice()
        
        // Start background location service
        startLocationService()
        
        // Navigate to dashboard
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    onAuthSuccess()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    onAuthSuccess()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun validateForm(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
} 