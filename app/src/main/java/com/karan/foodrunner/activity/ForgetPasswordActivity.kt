package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

            val phoneNum = etMobileNumber.text.toString()
            val email = etEmail.text.toString()

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/forgot _password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",phoneNum)
            jsonParams.put("email",email)

            if(ConnectionManager().checkConnectivity(this))
            {

                val jsonRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener { 
                    
                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success)
                        {
                            val intent = Intent(this,ResetPasswordActivity::class.java)
                            intent.putExtra("mobile_number",phoneNum)
                            startActivity(intent)
                        } else{
                            Toast.makeText(this, "Enter Correct Mobile Number", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e : Exception){
                        Toast.makeText(this, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                    }
                                                                                                                
                }, Response.ErrorListener {

                    Toast.makeText(this, "Some Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "957582bfd2ec65"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else{
                Toast.makeText(this, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}