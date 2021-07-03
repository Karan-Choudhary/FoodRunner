package com.karan.foodrunner.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etOTP :EditText
    lateinit var etNewPassword:EditText
    private lateinit var etConfirmPassword:EditText
    lateinit var btnSubmit:Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            MODE_PRIVATE)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if(etNewPassword.text.toString().equals(etConfirmPassword.text.toString()))
        {
//            proceed
            val phoneNum = intent.getStringExtra("mobile_number").toString()

            btnSubmit.setOnClickListener {

                val newPass = etNewPassword.text.toString()
                val otp = etOTP.text.toString()

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",phoneNum)
                jsonParams.put("password",newPass)
                jsonParams.put("otp",otp)

                if(ConnectionManager().checkConnectivity(this))
                {

                    val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if(success)
                            {
                                clearPreferences()
                                val intent = Intent(this,LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        } catch (e : Exception)
                        {
                            Toast.makeText(this, "Some error occurred !!!", Toast.LENGTH_SHORT).show()
                        }

                    },Response.ErrorListener {

                        Toast.makeText(this, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String,String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "957582bfd2ec65"
                            return headers
                        }
                    }

                } else{
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings"){_,_ ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        this?.finish()
                    }

                    dialog.setNegativeButton("Exit"){_,_ ->
                        ActivityCompat.finishAffinity(this as Activity)
                    }
                    dialog.create()
                    dialog.show()
                }


            }

        }
        else{
            Toast.makeText(this, "New Password and Confirm password is not same", Toast.LENGTH_SHORT).show()
        }



    }

    private fun clearPreferences()
    {
        sharedPreferences.edit().clear().apply()
    }
}