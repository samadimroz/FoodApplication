package com.imroz.foodapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.imroz.foodapplication.R

class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler= Handler()
        handler.postDelayed({
            val intent= Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        },1000)
    }
}