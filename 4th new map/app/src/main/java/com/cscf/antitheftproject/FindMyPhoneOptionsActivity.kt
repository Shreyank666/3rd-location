package com.cscf.antitheftproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FindMyPhoneOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_my_phone_options)

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnCurrentLocation).setOnClickListener {
            startActivity(Intent(this, LocationTrackingActivity::class.java))
        }

        findViewById<Button>(R.id.btnLocationHistory).setOnClickListener {
            startActivity(Intent(this, LocationHistoryActivity::class.java))
        }
    }
} 