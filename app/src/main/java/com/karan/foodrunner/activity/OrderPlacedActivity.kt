package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.karan.foodrunner.R
import com.karan.foodrunner.fragment.HomeFragment

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOk : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOk = findViewById(R.id.btnOK)

        btnOk.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        // user can't go back
    }

}