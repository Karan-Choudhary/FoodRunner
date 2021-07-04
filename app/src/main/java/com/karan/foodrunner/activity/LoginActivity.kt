package com.karan.foodrunner.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var cvMobile : CardView
    lateinit var cvPas : CardView
    lateinit var etMobileNumber : EditText
    lateinit var etPassword:EditText
    lateinit var txtForgetPassword:TextView
    lateinit var txtGoToSignUp:TextView
    lateinit var btnLogin:Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            MODE_PRIVATE)

        var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login)

        if(isLoggedIn)
        {
            var i1 = Intent(this,MainActivity::class.java)
            startActivity(i1)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgetPassword = findViewById(R.id.txtForgetPassword)
        txtGoToSignUp = findViewById(R.id.txtGoToSignUp)


        btnLogin.setOnClickListener{
            if(ConnectionManager().checkConnectivity(this))
            {
                if (checkForError())
                {
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/login/fetch_result/"

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number",etMobileNumber.text.toString())
                    jsonParams.put("password",etPassword.text.toString())

                    val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                        try {
                            val response = it.getJSONObject("data")
                            val success = response.getBoolean("success")

                            if(success)
                            {
                                val data = response.getJSONObject("data")
                                sharedPreferences.edit().clear().apply()
                                savePreferences(
                                    data.getString("user_id"),
                                    data.getString("name"),
                                    data.getString("email"),
                                    data.getString("mobile_number"),
                                    data.getString("address"))

                                val intent = Intent(this,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                val responseMessageServer =
                                    response.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e : Exception){
                            Toast.makeText(this, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this, "Some Error Occurred, Please try again Later(VolleyError)", Toast.LENGTH_SHORT).show()
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

        txtForgetPassword.setOnClickListener{
            val intent = Intent(this,ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        txtGoToSignUp.setOnClickListener {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

    }

    private fun savePreferences(user_id:String ,name:String, email:String, mobile_number:String, address:String) {

        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        sharedPreferences.edit().putString("user_id",user_id).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("mobile_number",mobile_number).apply()
        sharedPreferences.edit().putString("address",address).apply()

    }

    fun checkForError():Boolean{

        var noError = 0

        if(etMobileNumber.text.isBlank() || etMobileNumber.text.length!=10)
        {
            etMobileNumber.error = "Invalid Mobile Number"
        }else{
            noError++
        }

        if(etPassword.text.isBlank())
        {
            etPassword.error = "Enter Password"
        }else
        {
            noError++
        }

        return noError == 2

    }

}