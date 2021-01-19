package com.cnd.foodcordku.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.cnd.foodcordku.admin.AdminActivity
import com.cnd.foodcordku.admin.LoginAdminActivity
import com.cnd.foodcordku.databinding.ActivityPilihMejaBinding
import com.cnd.foodcordku.helper.UserPreferences
import com.cnd.foodcordku.util.Data
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PilihMejaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPilihMejaBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private val cameraPermissionRequestCode = 1
    var dataUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihMejaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val userId: String = myRef.push().key.toString()

        userPreferences.bookmark.asLiveData().observe(this, Observer {
            if (it == null){
                dataUser = ""
                Data.idUser = ""
            }else{
                dataUser = it.toString()
                Data.idUser = it.toString()
            }
        })

        binding.btnMeja.setOnClickListener {
//            Toast.makeText(this, "$dataUser", Toast.LENGTH_SHORT).show()

            if (dataUser.isNullOrEmpty() || dataUser.isNullOrBlank() || dataUser == "null") {
                startScanning()
                Data.takeAway = "No"
                Data.idUser = userId

//                Data.ID = "1"
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Perhatian..")
                    .setMessage("Anda Sudah Memesan Lanjut ke halaman tunggu?")
                    .setNegativeButton("Batal") { _, _ ->
                        null
                    }

                    .setPositiveButton("Ya") { _, _ ->
                        val intent = Intent(this, TungguActivity::class.java)
                        startActivity(intent)
                    }
                    .show()
            }

        }

        binding.btnTakeAway.setOnClickListener {
            startScanning()
            Data.takeAway = "yes"
            Data.idUser = userId

//            Data.ID = "2"
        }

        binding.btnAdmin.setOnClickListener {
            val intent = Intent(this, LoginAdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startScanning() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val i = Intent(this, ScanMejaActivity::class.java)
//            val i = Intent(this, MenuActivity::class.java)
            this.startActivity(i)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        }
    }
}