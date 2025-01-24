package com.cscf.antitheftproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LocationHistoryActivity : AppCompatActivity() {
    private val TAG = "LocationHistoryActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationHistoryAdapter
    private val locationHistoryList = mutableListOf<LocationData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_history)

        setupRecyclerView()
        fetchLocationHistory()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvLocationHistory)
        adapter = LocationHistoryAdapter(locationHistoryList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun fetchLocationHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("locations")
            .document(userId)
            .collection("history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50) // Show last 50 locations
            .get()
            .addOnSuccessListener { snapshot ->
                locationHistoryList.clear()
                snapshot.documents.forEach { doc ->
                    val locationData = doc.toObject(LocationData::class.java)
                    locationData?.let {
                        locationHistoryList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching location history", e)
                Toast.makeText(this, "Failed to load location history", Toast.LENGTH_SHORT).show()
            }
    }
} 