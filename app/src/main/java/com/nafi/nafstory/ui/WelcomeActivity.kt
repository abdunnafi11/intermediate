package com.nafi.nafstory.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nafi.nafstory.R
import com.nafi.nafstory.utils.PrefsManager

class WelcomeActivity : AppCompatActivity() {

    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Initialize PrefsManager to manage navigation logic
        prefsManager = PrefsManager(this)

        supportActionBar?.hide()

        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            // Navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
