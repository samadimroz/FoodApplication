 package com.imroz.foodapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.imroz.foodapplication.R
import com.imroz.foodapplication.util.ConnectionManager
import com.imroz.foodapplication.util.Validations
import org.json.JSONObject

 class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etForgotMobileNumber: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnForgotNext: Button
    lateinit var rlContentMain: RelativeLayout
     lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etForgotMobileNumber= findViewById(R.id.etForgotMobileNumber)
        etForgotEmail= findViewById(R.id.etForgotEmail)
        btnForgotNext= findViewById(R.id.btnForgotNext)
        rlContentMain = findViewById(R.id.rlContentMain)
        progressBar = findViewById(R.id.progressBar)
        rlContentMain.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        btnForgotNext.setOnClickListener{

//            val intent= Intent(this@ForgotPasswordActivity, MainActivity::class.java)
//            val bundle= Bundle()
//            bundle.putString("data", "forgot")
//            bundle.putString("mobile", etForgotMobileNumber.text.toString())
//            bundle.putString("email", etForgotEmail.text.toString())
//            intent.putExtra("details", bundle)
//            startActivity(intent)
            val forgotMobileNumber = etForgotMobileNumber.text.toString()
            if (Validations.validateMobile(forgotMobileNumber)) {
                etForgotMobileNumber.error = null
                if (Validations.validateEmail(etForgotEmail.text.toString())) {
                    if (ConnectionManager().isNetworkAvailable(this@ForgotPasswordActivity)) {
                        rlContentMain.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        sendOTP(etForgotMobileNumber.text.toString(), etForgotEmail.text.toString())
                    } else {
                        rlContentMain.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "No Internet Connection!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    rlContentMain.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    etForgotEmail.error = "Invalid Email"
                }
            } else {
                rlContentMain.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                etForgotMobileNumber.error = "Invalid Mobile Number"
            }
        }
    }

     private fun sendOTP(mobileNumber: String, email: String) {
         val queue = Volley.newRequestQueue(this)
         val FORGOT_PASSWORD= "http://13.235.250.119/v2/forgot_password/fetch_result"

         val jsonParams = JSONObject()
         jsonParams.put("mobile_number", mobileNumber)
         jsonParams.put("email", email)

         val jsonObjectRequest =
             object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams, Response.Listener {
                 try {
                     val data = it.getJSONObject("data")
                     val success = data.getBoolean("success")
                     if (success) {
                         val firstTry = data.getBoolean("first_try")
                         if (firstTry) {
                             val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                             builder.setTitle("Information")
                             builder.setMessage("Please check your registered Email for the OTP.")
                             builder.setCancelable(false)
                             builder.setPositiveButton("Ok") { _, _ ->
                                 val intent = Intent(
                                     this@ForgotPasswordActivity,
                                     ResetPasswordActivity::class.java
                                 )
                                 intent.putExtra("user_mobile", mobileNumber)
                                 startActivity(intent)
                             }
                             builder.create().show()
                         } else {
                             val builder = AlertDialog.Builder(this@ForgotPasswordActivity)
                             builder.setTitle("Information")
                             builder.setMessage("Please refer to the previous email for the OTP.")
                             builder.setCancelable(false)
                             builder.setPositiveButton("Ok") { _, _ ->
                                 val intent = Intent(
                                     this@ForgotPasswordActivity,
                                     ResetPasswordActivity::class.java
                                 )
                                 intent.putExtra("user_mobile", mobileNumber)
                                 startActivity(intent)
                             }
                             builder.create().show()
                         }
                     } else {
                         rlContentMain.visibility = View.VISIBLE
                         progressBar.visibility = View.GONE
                         Toast.makeText(
                             this@ForgotPasswordActivity,
                             "Mobile number not registered!",
                             Toast.LENGTH_SHORT
                         ).show()
                     }
                 } catch (e: Exception) {
                     e.printStackTrace()
                     rlContentMain.visibility = View.VISIBLE
                     progressBar.visibility = View.GONE
                     Toast.makeText(
                         this@ForgotPasswordActivity,
                         "Incorrect response error!!",
                         Toast.LENGTH_SHORT
                     ).show()
                 }
             }, Response.ErrorListener {
                 rlContentMain.visibility = View.VISIBLE
                 progressBar.visibility = View.GONE
                 VolleyLog.e("Error::::", "/post request fail! Error: ${it.message}")
                 Toast.makeText(this@ForgotPasswordActivity, it.message, Toast.LENGTH_SHORT).show()
             }) {
                 override fun getHeaders(): MutableMap<String, String> {
                     val headers = HashMap<String, String>()
                     headers["Content-type"] = "application/json"
                     headers["token"] = "d3f7c22906be8c"
                     return headers
                 }
             }
         queue.add(jsonObjectRequest)

     }
}