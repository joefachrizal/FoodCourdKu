package com.cnd.foodcordku.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cnd.foodcordku.databinding.ActivityLoginAdminBinding
import com.cnd.foodcordku.util.Data

class LoginAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val inUser: String = binding.userAdmin.text.toString().trim()
            val inPass: String = binding.passAdmin.text.toString()
            if (inUser.isEmpty()) {
                binding.userAdmin.error = "user kosong"
            }
            if (inPass.isEmpty()) {
                binding.passAdmin.error = "pass kosong"
            }
            if (inUser == Data.USERDATA && inPass == Data.PASSWORD) {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "ID salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}