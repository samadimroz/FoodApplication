package com.imroz.foodapplication.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.imroz.foodapplication.R
import com.imroz.foodapplication.util.ConnectionManager
import com.imroz.foodapplication.util.SessionManager
import com.imroz.foodapplication.util.Validations
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var progressBar: ProgressBar
    lateinit var rlRegister: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sessionManager: SessionManager

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_registration)
//        etName = findViewById(R.id.etName)
//        etEmail = findViewById(R.id.etEmail)
//        etMobileNumber = findViewById(R.id.etMobileNumber)
//        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
//        etPassword = findViewById(R.id.etPassword)
//        etConfirmPassword = findViewById(R.id.etConfirmPassword)
//        btnRegister = findViewById(R.id.btnRegister)
//
//        btnRegister.setOnClickListener {
//            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
//            val bundle = Bundle()
//            bundle.putString("data", "register")
//            bundle.putString("name", etName.text.toString())
//            bundle.putString("mobile", etMobileNumber.text.toString())
//            bundle.putString("password", etPassword.text.toString())
//            bundle.putString("address", etDeliveryAddress.text.toString())
//            intent.putExtra("details", bundle)
//            startActivity(intent)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sessionManager = SessionManager(this@RegistrationActivity)
        sharedPreferences = this@RegistrationActivity.getSharedPreferences(sessionManager.PREF_NAME, sessionManager.PRIVATE_MODE)
        rlRegister = findViewById(R.id.rlRegister)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        rlRegister.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE


        btnRegister.setOnClickListener {
            rlRegister.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            if (Validations.validateNameLength(etName.text.toString())) {
                etName.error = null
                if (Validations.validateEmail(etEmail.text.toString())) {
                    etEmail.error = null
                    if (Validations.validateMobile(etMobileNumber.text.toString())) {
                        etMobileNumber.error = null
                        if (Validations.validatePasswordLength(etPassword.text.toString())) {
                            etPassword.error = null
                            if (Validations.matchPassword(
                                    etPassword.text.toString(),
                                    etConfirmPassword.text.toString()
                                )
                            ) {
                                etPassword.error = null
                                etConfirmPassword.error = null
                                if (ConnectionManager().isNetworkAvailable(this@RegistrationActivity)) {
                                    sendRegisterRequest(
                                        etName.text.toString(),
                                        etMobileNumber.text.toString(),
                                        etDeliveryAddress.text.toString(),
                                        etPassword.text.toString(),
                                        etEmail.text.toString()
                                    )
                                } else {
                                    rlRegister.visibility = View.VISIBLE
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(this@RegistrationActivity, "No Internet Connection", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                rlRegister.visibility = View.VISIBLE
                                progressBar.visibility = View.INVISIBLE
                                etPassword.error = "Passwords don't match"
                                etConfirmPassword.error = "Passwords don't match"
                                Toast.makeText(this@RegistrationActivity, "Passwords don't match", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            rlRegister.visibility = View.VISIBLE
                            progressBar.visibility = View.INVISIBLE
                            etPassword.error = "Password should be more than or equal 4 digits"
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Password should be more than or equal 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        etMobileNumber.error = "Invalid Mobile number"
                        Toast.makeText(this@RegistrationActivity, "Invalid Mobile number", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    etEmail.error = "Invalid Email"
                    Toast.makeText(this@RegistrationActivity, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
            } else {
                rlRegister.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                etName.error = "Invalid Name"
                Toast.makeText(this@RegistrationActivity, "Invalid Name", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendRegisterRequest(name: String, phone: String, address: String, password: String, email: String) {

        val queue = Volley.newRequestQueue(this)
        val REGISTER= "http://13.235.250.119/v2/register/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", phone)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            REGISTER,
            jsonParams,
            Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val response = data.getJSONObject("data")
                        sharedPreferences.edit()
                            .putString("user_id", response.getString("user_id")).apply()
                        sharedPreferences.edit()
                            .putString("user_name", response.getString("name")).apply()
                        sharedPreferences.edit()
                            .putString(
                                "user_mobile_number",
                                response.getString("mobile_number")
                            )
                            .apply()
                        sharedPreferences.edit()
                            .putString("user_address", response.getString("address"))
                            .apply()
                        sharedPreferences.edit()
                            .putString("user_email", response.getString("email")).apply()
                        sessionManager.setLogin(true)
                        startActivity(
                            Intent(
                                this@RegistrationActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        rlRegister.visibility = View.VISIBLE
                        progressBar.visibility = View.INVISIBLE
                        val errorMessage = data.getString("errorMessage")
                        Toast.makeText(
                            this@RegistrationActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception){
                    rlRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@RegistrationActivity, it.message, Toast.LENGTH_SHORT).show()
                rlRegister.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "d3f7c22906be8c"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    override fun onSupportNavigateUp(): Boolean {
        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
        onBackPressed()
        return true
    }

}