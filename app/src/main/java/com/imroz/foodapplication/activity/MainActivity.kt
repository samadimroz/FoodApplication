package com.imroz.foodapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.imroz.foodapplication.R
import com.imroz.foodapplication.adapter.RestaurantMenuAdapter
import com.imroz.foodapplication.fragment.*
import com.imroz.foodapplication.fragment.RestaurantFragment.Companion.resId
import com.imroz.foodapplication.util.DrawerLocker
import com.imroz.foodapplication.util.SessionManager

class MainActivity : AppCompatActivity(), DrawerLocker {

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
    }

    lateinit var txtData: TextView
    lateinit var btnLogout: Button
    lateinit var drawerLayout: DrawerLayout

    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedPrefs: SharedPreferences

    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this@MainActivity)
        sharedPrefs = this@MainActivity.getSharedPreferences(
            sessionManager.PREF_NAME,
            sessionManager.PRIVATE_MODE
        )

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frameLayout)

        setToolbar()

        openDashboard()

        setupActionBarToggle()

        lateinit var sharedPreferences: SharedPreferences

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId){
                R.id.home ->{
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment())
                        .commit()
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.order_history ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title="Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment())
                        .commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.faq ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FaqFragment())
                        .commit()
                    supportActionBar?.title="About App"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    sharedPreferences= getSharedPreferences("Food preference", Context.MODE_PRIVATE)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sessionManager.setLogin(false)
                            sharedPreferences.edit().clear().apply()
                            val intent= Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)

                            Toast.makeText(
                                this@MainActivity,
                                "Succesfully Logged Out",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        .setNegativeButton("No") { _, _ ->
                            HomeFragment()
                        }
                        .create()
                        .show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

//        val convertView = LayoutInflater.from(this@MainActivity).inflate(R.layout.drawer_header, null)
//        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
//        val userPhone: TextView = convertView.findViewById(R.id.txtDrawerSecondaryText)
//        val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
//        userName.text = sharedPrefs.getString("user_name", null)
//        val phoneText = "+91-${sharedPrefs.getString("user_mobile_number", null)}"
//        userPhone.text = phoneText
//        navigationView.addHeaderView(convertView)
//
//        userName.setOnClickListener {
//            val profileFragment = ProfileFragment()
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.frameLayout, profileFragment)
//            transaction.commit()
//            supportActionBar?.title = "My profile"
//            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
//            Handler().postDelayed(mPendingRunnable, 50)
//        }
//
//        appIcon.setOnClickListener {
//            val profileFragment = ProfileFragment()
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.frameLayout, profileFragment)
//            transaction.commit()
//            supportActionBar?.title = "My profile"
//            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
//            Handler().postDelayed(mPendingRunnable, 50)
//        }


//        sharedPreferences= getSharedPreferences("Food preference",Context.MODE_PRIVATE)
//        btnLogout=findViewById(R.id.btnLogout)
//        btnLogout.setOnClickListener{
//            sharedPreferences.edit().clear().apply()
//            val intent= Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
//            Toast.makeText(this@MainActivity,"Succesfully Logged Out",Toast.LENGTH_LONG).show()
//            finish()
//
//        }
//
//        txtData = findViewById(R.id.txtData)

        /*Checking whether any data was received through the intent or not*/
//        if (intent!= null) {
//
//            /*Fetching the details from the intent*/
//            val details = intent.getBundleExtra("details")
//
//            /*Getting the value of data from the bundle object*/
//            val data = details?.getString("data")
//
//            /*Checking the location from which data was sent*/
//            if (data == "login") {
//                /*Creating the text to be displayed*/
//                val text = "Mobile Number : ${details.getString("mobile")} \n " +
//                        "Password : ${details.getString("password")}"
//                txtData.text = text
//            }
//
//            if (data == "register") {
//                val text = " Name : ${details.getString("name")} \n " +
//                        "E-mail : ${details.getString("email")} \n" +
//                        "Mobile Number : ${details.getString("mobile")} \n " +
//                        "Password : ${details.getString("password")} \n " +
//                        "Address: ${details.getString("address")}"
//                txtData.text = text
//            }
//
//            if (data == "forgot") {
//                val text = "Mobile Number : ${details.getString("mobile")} \n " +
//                        "E-mail : ${details.getString("email")}"
//                txtData.text = text
//            }
//
//        } else {
//            /*No data was received through the intent*/
//            txtData.text = "No data received !!!"
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id= item.itemId
        if (id== android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun setToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.title="Foody Hut"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun openDashboard() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }


    private fun setupActionBarToggle(){
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        ){
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val pendingRunnable = Runnable {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }

                Handler().postDelayed(pendingRunnable, 50)
            }
        }

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (f) {
            is HomeFragment -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is RestaurantFragment -> {
                if (!RestaurantMenuAdapter.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Going back will reset cart items. Do you still want to proceed?")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                MyCartActivity.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            openDashboard()
                            RestaurantMenuAdapter.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    openDashboard()
                }
            }
            else -> openDashboard()
        }
    }

}