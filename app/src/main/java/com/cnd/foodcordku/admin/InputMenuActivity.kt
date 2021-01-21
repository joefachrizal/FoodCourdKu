package com.cnd.foodcordku.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cnd.foodcordku.databinding.ActivityInputMenuBinding
import com.cnd.foodcordku.model.DataMenu
import com.cnd.foodcordku.util.Data
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InputMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        binding.proses.setOnClickListener {
            onGetData()
        }
    }

    private fun onGetData() {
        val titleFood = binding.namaMakanan.text.toString()
        val price = binding.hargaMakanan.text.toString()
        val desc = binding.deskripsiMakanan.text.toString()
        val image = binding.inputUrlGambar.text.toString()
        val key = myRef.push().key.toString()

        if (titleFood.isEmpty()) {
            binding.namaMakanan.error = "tidak boleh kosong"
        }
        if (price.isEmpty()) {
            binding.hargaMakanan.error = "tidak boleh kosong"
        }
        if (desc.isEmpty()) {
            binding.deskripsiMakanan.error = "tidak boleh kosong"
        }
        if (image.isEmpty()) {
            binding.inputUrlGambar.error = "tidak boleh kosong"
        } else {
            onPushData(
                DataMenu(
                    titleFood,
                    price,
                    desc,
                    "5",
                    key,
                    "moga bunda resto",
                    image
                )
            )
        }
    }

    private fun onPushData(data: DataMenu) {
        myRef.child(Data.MENU)
            .child(data.keyMakanan.toString())
            .setValue(data)
            .addOnSuccessListener(this) {
                binding.namaMakanan.setText("")
                binding.hargaMakanan.setText("")
                binding.deskripsiMakanan.setText("")
                binding.inputUrlGambar.setText("")
                Snackbar.make(
                    binding.proses,
                    "Data berhasil ditambahkan",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
    }
}