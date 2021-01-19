package com.cnd.foodcordku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.cnd.foodcordku.databinding.ActivitySplashScreenBinding
import com.cnd.foodcordku.user.PilihMejaActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, PilihMejaActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_OUT.toLong())
    }

    companion object {
        private const val TIME_OUT = 3000
    }
}