package com.karan.foodrunner.activity

import android.app.Activity
import android.content.Intent
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
import java.lang.Exception

class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var etMobileNumber : EditText
    lateinit var etEmail: EditText
    lateinit var btnNext : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener{
            if(ConnectionManager().checkConnectivity(this))
            {

                if(checkForError())
                {
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",etMobileNumber.text.toString())
                    jsonParams.put("email",etEmail.text.toString())

                    val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                        try{
                            val response = it.getJSONObject("data")
                            val success = response.getBoolean("success")
                            if(success)
                            {

                                val firstTry = response.getBoolean("first_try")
                                val intent = Intent(this, ResetPasswordActivity::class.java)
                                intent.putExtra("mobile_number", etMobileNumber.text.toString())

                                if(firstTry) {
                                    Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                } else{
                                    Toast.makeText(this, "OTP sent Already", Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                }
                            } else{
                                val responseMessageServer =
                                    response.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()                            }
                        } catch (e : Exception){
                            Toast.makeText(this, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this, "Enter Correct Details!!!", Toast.LENGTH_SHORT).show()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String,String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "957582bfd2ec65"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
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

    fun checkForError():Boolean{

        var noError = 0

        if(etMobileNumber.text.isBlank() || etMobileNumber.text.length!=10){
            etMobileNumber.error = "Invalid Mobile Number"
        }else
        {
            noError++
        }

        if(etEmail.text.isBlank()){
            etEmail.error = "Email Missing!"
        }else
        {
            noError++
        }

        return noError == 2
    }

}