package com.cnd.foodcordku.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.cnd.foodcordku.R
import com.cnd.foodcordku.adapter.KeranjangAdapter
import com.cnd.foodcordku.adapter.PemesanAdapter
import com.cnd.foodcordku.databinding.ActivityListPemesanBinding
import com.cnd.foodcordku.model.DataPemesan
import com.cnd.foodcordku.user.KeranjangActivity
import com.cnd.foodcordku.user.MenuActivity
import com.cnd.foodcordku.util.Data
import com.google.firebase.database.*

class ListPemesanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListPemesanBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var adapter: PemesanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPemesanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        initData()
    }

    private fun initData() {
        val animationType = R.anim.layout_animation_fall_down
        val animation = AnimationUtils.loadLayoutAnimation(this, animationType)
        with(binding) {
            listPemesan.layoutAnimation = animation
            listPemesan.adapter?.notifyDataSetChanged()
            listPemesan.scheduleLayoutAnimation()
            listPemesan.setHasFixedSize(true)
        }

        onGetData()
    }

    private fun onGetData() {
        myRef.child(Data.KONFIRMASI)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listPemsan: ArrayList<DataPemesan> = arrayListOf()
                    for (dataSnapshot1 in snapshot.children) {
                        val dataPemesan = dataSnapshot1.getValue(DataPemesan::class.java)
                        dataPemesan?.let { listPemsan.add(it) }
                    }
                    onShowData(listPemsan)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun onShowData(listPemsan: ArrayList<DataPemesan>) {
        adapter = PemesanAdapter(listPemsan)
        binding.listPemesan.adapter = adapter

        adapter.setOnItemClickCallback(object : PemesanAdapter.OnItemClickCallback {
            override fun onClicked(data: DataPemesan) {
                Data.takeAway = data.takeAway.toString()
                Data.ID = data.meja.toString()
                Data.idUser = data.iduser.toString()
                Data.akses = "admin"
                val i = Intent(this@ListPemesanActivity, KeranjangActivity::class.java)
                startActivity(i)
            }

            override fun onSlide(data: DataPemesan, konfirmasi: String) {
                myRef.child(Data.KONFIRMASI).child(data.iduser.toString()).child("statusPesanan")
                    .setValue(konfirmasi)
            }
        })
    }
}