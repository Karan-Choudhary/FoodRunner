package com.karan.foodrunner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.karan.foodrunner.R

class Register : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout

    lateinit var etName : EditText
    lateinit var etEmailAddress : EditText
    lateinit var etMobileNumber : EditText
    lateinit var etDeliveryAddress : EditText
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnRegister: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)

        etName = findViewById(R.id.etName)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        setUpToolbar()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}