package com.example.firebaseauthdemo

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.firebaseauthdemo.fragments.DashboardFragment
import com.example.firebaseauthdemo.fragments.OperationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val dashboardFragment = DashboardFragment()
        val operationsFragment = OperationsFragment()

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        replaceFragment(dashboardFragment)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_logout -> logout(auth)
                R.id.ic_dashboard -> replaceFragment(dashboardFragment)
                R.id.ic_operations -> replaceFragment(operationsFragment)
                else -> replaceFragment(dashboardFragment)
            }
            true
        }

    }
    private fun logout(auth: FirebaseAuth) {
        // Logout from the app
        auth.signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

}