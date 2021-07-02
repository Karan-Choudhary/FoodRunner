package com.karan.foodrunner.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R

class LoginActivity : AppCompatActivity() {

    lateinit var cvMobile : CardView
    lateinit var cvPas : CardView
    lateinit var etMobileNumber : EditText
    lateinit var etPassword:EditText
    lateinit var txtForgetPassword:TextView
    lateinit var txtGoToSignUp:TextView
    lateinit var btnLogin:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgetPassword = findViewById(R.id.txtForgetPassword)
        txtGoToSignUp = findViewById(R.id.txtGoToSignUp)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/login/fetch_result/"




    }
}