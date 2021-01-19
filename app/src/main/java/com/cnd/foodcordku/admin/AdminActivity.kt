package com.cnd.foodcordku.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cnd.foodcordku.databinding.ActivityAdminBinding
import com.cnd.foodcordku.user.MenuActivity
import com.cnd.foodcordku.util.Data

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnListStok.setOnClickListener {
            Data.akses = "admin"
            val i = Intent(this, MenuActivity::class.java)
            this.startActivity(i)
        }

        binding.btnListPemesan.setOnClickListener {
            val i = Intent(this, ListPemesanActivity::class.java)
            this.startActivity(i)
        }
    }
}