package com.karan.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.karan.foodrunner.R
import com.karan.foodrunner.fragment.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView

    lateinit var txtUserName:TextView
    lateinit var txtUserNum:TextView

    private var previousMenuItem:MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        val headerView = navigationView.getHeaderView(0)
        txtUserName = headerView.findViewById(R.id.txtUserName)
        txtUserNum = headerView.findViewById(R.id.txtUserNum)

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
            MODE_PRIVATE)

        setUpToolbar()

        txtUserName.text = sharedPreferences.getString("name","User")
        txtUserNum.text = sharedPreferences.getString("mobile_number","0000000000")

        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,
        R.string.open_drawer,
        R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem!=null)
            {
                previousMenuItem?.isChecked=false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId)
            {
                R.id.goHome -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        MyProfileFragment()
                    ).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourite_Restaurants -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame,FavouriteRestaurantsFragment()).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.order_history -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame,OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame,FaqsFragment()).commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
//                    supportFragmentManager.beginTransaction().replace(R.id.frame,LogoutFragment()).commit()
//                    supportActionBar?.title = "Logout"
                    drawerLayout.closeDrawers()

                    val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),
                        MODE_PRIVATE)

                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit?")
                    dialog.setPositiveButton("YES"){_,_ ->
                        sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                        this.finish()
                    }
                    dialog.setNegativeButton("NO"){_,_ ->
                        openHome()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openHome()
    {
        val fragment = HomeFragment(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment).commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.goHome)
    }

    override fun onBackPressed() {

        when(supportFragmentManager.findFragmentById(R.id.frame))
        {
            !is HomeFragment -> {
                openHome()
                drawerLayout.closeDrawers()
            }
            else -> super.onBackPressed()
        }
    }

}