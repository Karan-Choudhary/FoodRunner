package com.karan.foodrunner.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.karan.foodrunner.R
import com.karan.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

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
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            MODE_PRIVATE)

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

            btnRegister.setOnClickListener {
                if(ConnectionManager().checkConnectivity(this))
                {
                    if(checkForErrors())
                    {
                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/register/fetch_result"

                        val jsonParams = JSONObject()
                        jsonParams.put("name",etName.text.toString())
                        jsonParams.put("mobile_number",etMobileNumber.text.toString())
                        jsonParams.put("password",etPassword.text.toString())
                        jsonParams.put("address",etDeliveryAddress.text.toString())
                        jsonParams.put("email",etEmailAddress.text.toString())

                        val jsonObjectRequest = object: JsonObjectRequest(Method.POST,url, jsonParams, Response.Listener {

                            try{
                                val response =  it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if(success)
                                {
                                    val data = response.getJSONObject("data")

                                    savePreferences(
                                        data.getString("user_id"),
                                        data.getString("name"),
                                        data.getString("email"),
                                        data.getString("mobile_number"),
                                        data.getString("address")
                                    )

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
                            }catch (e : Exception){
                                Toast.makeText(this, "INVALID", Toast.LENGTH_SHORT).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
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
                }else{
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

    private fun savePreferences(user_id:String ,name:String, email:String, mobile_number:String, address:String) {
        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        sharedPreferences.edit().putString("user_id",user_id).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("mobile_number",mobile_number).apply()
        sharedPreferences.edit().putString("address",address).apply()

    }

    private fun checkForErrors():Boolean{
        var noError = 0

        if(etName.text.isBlank()){
            etName.error = "Name Missing!"
        }else{
            noError++
        }

        if(etMobileNumber.text.isBlank() || etMobileNumber.text.length!=10){
            etMobileNumber.error = "Invalid Mobile Number"
        }else
        {
            noError++
        }

        if(etEmailAddress.text.isBlank()){
            etEmailAddress.error = "Email Missing!"
        }else
        {
            noError++
        }

        if(etDeliveryAddress.text.isBlank()){
            etDeliveryAddress.error = "Address Missing!"
        }else
        {
            noError++
        }

        if(etPassword.text.isBlank() || etPassword.text.length <= 4){
            etPassword.error = "Invalid Password"
        }else
        {
            noError++
        }

        if(etConfirmPassword.text.isBlank() && etConfirmPassword.text.toString().equals(etPassword.text.toString())){
            etConfirmPassword.error = "Field Missing or password does;t match!"
        }else
        {
            noError++
        }

        return noError == 6
    }


}