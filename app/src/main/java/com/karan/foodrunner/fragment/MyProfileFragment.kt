package com.karan.foodrunner.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.karan.foodrunner.R


class MyProfileFragment : Fragment() {

    lateinit var txtName:TextView
    lateinit var txtMobileNumber:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        txtName.text = sharedPreferences.getString("name","User")
        txtMobileNumber.text = sharedPreferences.getString("mobile_number","")
        txtEmail.text = sharedPreferences.getString("email","")
        txtAddress.text = sharedPreferences.getString("address","")

        return view
    }

    override fun onAttach(context: Context) {
        sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE)
        super.onAttach(context)

    }
}