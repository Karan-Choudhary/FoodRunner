package com.karan.foodrunner.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
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

    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            MODE_PRIVATE)

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        progressLayout = findViewById(R.id.progress_Layout)
        progressLayout.visibility = View.VISIBLE
        progressBar = findViewById(R.id.progressBar)

            if(intent!=null)
            {
                progressLayout.visibility = View.GONE
                btnSubmit.setOnClickListener {

                        if(ConnectionManager().checkConnectivity(this))
                        {

                            if(checkForErrors())
                            {
                                val queue = Volley.newRequestQueue(this)
                                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                val jsonParams = JSONObject()
                                jsonParams.put("mobile_number",intent.getStringExtra("mobile_number").toString())
                                jsonParams.put("password",etNewPassword.text.toString())
                                jsonParams.put("otp",etOTP.text.toString())

                                val jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                                    try {

                                        val response = it.getJSONObject("data")
                                        val success = response.getBoolean("success")

                                        if(success)
                                        {
                                            val successMessage =
                                                response.getString("successMessage")

                                            clearPreferences()
                                            val intent = Intent(this,LoginActivity::class.java)
                                            startActivity(intent)
                                            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                                            finish()

                                        } else{
                                            val responseMessageServer =
                                                response.getString("errorMessage")
                                            Toast.makeText(
                                                this,
                                                responseMessageServer.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
//                                            progressLayout.visibility = View.GONE
//                                            progressBar.visibility = View.GONE
                                        }

                                    } catch (e : Exception)
                                    {
                                        Toast.makeText(this, "Some error occurred !!!", Toast.LENGTH_SHORT).show()
//                                        progressLayout.visibility = View.GONE
//                                        progressBar.visibility = View.GONE
                                    }

                                },Response.ErrorListener {

                                    Toast.makeText(this, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
//                                    progressLayout.visibility = View.GONE
//                                    progressBar.visibility = View.GONE

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

            } else {
                Toast.makeText(this, "Some Unexpected Error occurred please try again later", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearPreferences()
    {
        sharedPreferences.edit().clear().apply()
    }

    fun checkForErrors():Boolean{
        var noError = 0

        if(etOTP.text.isBlank() || etOTP.text.toString().length!=4)
        {
            etOTP.error = "Something is Wrong"
        }else{
            noError++
        }

        if(etNewPassword.text.isBlank() || etNewPassword.text.length <= 4){
            etNewPassword.error = "Invalid Password"
        }else
        {
            noError++
        }

        if(etConfirmPassword.text.isBlank() && etConfirmPassword.text.toString().equals(etNewPassword.text.toString())){
            etConfirmPassword.error = "Field Missing or password does;t match!"
        }else
        {
            noError++
        }
        return noError==3
    }
}