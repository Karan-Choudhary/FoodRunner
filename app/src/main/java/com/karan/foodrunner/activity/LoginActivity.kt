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

        val phoneNum:String? = etMobileNumber.text.toString()
        val pass:String? = etPassword.text.toString()

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/login/fetch_result/"

        btnLogin.setOnClickListener{

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",phoneNum)
            jsonParams.put("password",pass)


            if(ConnectionManager().checkConnectivity(this))
            {
                val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if(success)
                        {
                            val mainData = data.getJSONObject("data")
                            savePreferences(
                                mainData.getString("user_id"),
                                mainData.getString("name"),
                                mainData.getString("email"),
                                mainData.getString("mobile_number"),
                                mainData.getString("address"))

                            val intent = Intent(this,MainActivity::class.java)
                            finish()
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "INVALID Credentials", Toast.LENGTH_SHORT).show()
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

}