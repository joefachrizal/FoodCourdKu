package com.cnd.foodcordku.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cnd.foodcordku.R
import com.cnd.foodcordku.adapter.MenuAdapter
import com.cnd.foodcordku.admin.InputMenuActivity
import com.cnd.foodcordku.databinding.ActivityMenuBinding
import com.cnd.foodcordku.model.DataMenu
import com.cnd.foodcordku.model.DataTemp
import com.cnd.foodcordku.util.Data
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_loading.*
import kotlinx.android.synthetic.main.sheet_jumlah_pesan.*
import kotlinx.android.synthetic.main.sheet_pesan.*
import java.text.NumberFormat
import java.util.*

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var localeID: Locale
    private lateinit var formatRupiah: NumberFormat

    private var harga = ""
    private var total = ""
    private var totalPesanan = ""
    private var jmlh = 0
    private var jmlhStok = 0

    private lateinit var sheetJumPesan: BottomSheetBehavior<ConstraintLayout>
    private lateinit var sheetPesan: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        setSupportActionBar(binding.toolbar)

        localeID = Locale("in", "ID")
        formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        if (Data.akses == "admin") {
            binding.btnAdd.visibility = View.VISIBLE
            binding.btnAdd.setOnClickListener {
                val i = Intent(this, InputMenuActivity::class.java)
                this.startActivity(i)
            }
        } else {
            binding.btnAdd.visibility = View.GONE
            onGetPesanan()
        }

        loading_data.visibility = View.VISIBLE

        initData()
        onGetData()
    }

    private fun initData() {
        sheetJumPesan = BottomSheetBehavior.from(sheet_jum_pesan)
        sheetPesan = BottomSheetBehavior.from(sheet_pesan)

        if (Data.jumlahPesanan.isNotEmpty() && Data.jumlahPesanan != "0") {
            sheetJumPesan.state = BottomSheetBehavior.STATE_HIDDEN
            sheetPesan.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            sheetJumPesan.state = BottomSheetBehavior.STATE_HIDDEN
            sheetPesan.state = BottomSheetBehavior.STATE_HIDDEN
        }

        val animationType = R.anim.layout_animation_fall_down
        val animation = AnimationUtils.loadLayoutAnimation(this, animationType)
        with(binding) {
            listMenu.layoutAnimation = animation
            listMenu.adapter?.notifyDataSetChanged()
            listMenu.scheduleLayoutAnimation()
            listMenu.setHasFixedSize(true)
        }
    }

    private fun onGetData() {
        myRef.child(Data.MENU)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listMenu: ArrayList<DataMenu> = arrayListOf()
                    for (dataSnapshot1 in snapshot.children) {
                        val dataMenu = dataSnapshot1.getValue(DataMenu::class.java)
                        dataMenu?.let { listMenu.add(it) }
                    }
                    onShowData(listMenu)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun onShowData(listData: ArrayList<DataMenu>) {
        menuAdapter = MenuAdapter(listData)
        binding.listMenu.adapter = menuAdapter
        loading_data.visibility = View.GONE

        menuAdapter.setOnItemClickCallback(object : MenuAdapter.OnItemClickCallback {
            override fun onClicked(data: DataMenu) {
                jmlhStok = data.stokMakanan?.toInt() ?: 0
                if (jmlhStok.toString() != "0") {
                    name_food_pop.text = data.titleFood
                    price_food_pop.text = formatRupiah.format(data.price?.toDouble())
                    description_pop.text = data.description
                    harga = data.price.toString()
                    Data.key = data.keyMakanan.toString()

                    val state =
                        if (sheetJumPesan.state != BottomSheetBehavior.STATE_EXPANDED)
                            BottomSheetBehavior.STATE_EXPANDED
                        else
                            BottomSheetBehavior.STATE_HIDDEN
                    sheetJumPesan.state = state

                    if (Data.akses != "admin") {
                        jmlh = 0 // reset to 0
                        keranjang.text = getString(R.string.comment_to_reset)
                        Data.namaPesanan = data.titleFood.toString()
                    } else {
                        jmlh = data.stokMakanan?.toInt() ?: 0
                        keranjang.text = "tambah Stok"
                    }
                    jumlah.text = jmlh.toString()

                    subFood()
                    sumFood()
                    onKeranjang()
                } else {
                    MaterialAlertDialogBuilder(this@MenuActivity)
                        .setTitle("Perhatian..")
                        .setMessage("Sayangnya Stok Sudah Habis...\nSilahkan pilih menu yang tersedia")
                        .show()
                }
            }
        })
    }

    private fun subFood() {
        min.setOnClickListener {
            if (jmlh < 1) {
                keranjang.text = getString(R.string.comment_back_to)
            } else if (jmlh > 0) {
                jmlh -= 1
                jumlah.text = jmlh.toString()
                if (Data.akses != "admin") {
                    total = (jmlh * harga.toInt()).toString()
                    val totalRp = formatRupiah.format(total.toDouble())
                    val btnText = "Tambah Ke Keranjang - $totalRp"
                    keranjang.text = btnText
                }
            }
        }
    }

    private fun sumFood() {
        max.setOnClickListener {
            jmlh += 1
            jumlah.text = jmlh.toString()
            if (Data.akses != "admin") {
                total = (jmlh * harga.toInt()).toString()
                val totalRp = formatRupiah.format(total.toDouble())
                val btnText = "Tambah Ke Keranjang - $totalRp"
                keranjang.text = btnText
            }
        }
    }

    private fun onKeranjang() {
        keranjang.setOnClickListener {
            if (Data.akses != "admin") {
                Data.jumlahPesanan = jmlh.toString()
                Data.hargaPesanan = total
                if (jmlh > 0) {
                    submitMenu(
                        DataTemp(
                            Data.namaPesanan,
                            Data.jumlahPesanan,
                            Data.hargaPesanan
                        )
                    )
                    val sisaStock = jmlhStok - jmlh
                    myRef.child(Data.MENU).child(Data.key).child("stokMakanan")
                        .setValue(sisaStock.toString())
                } else {
                    Toast.makeText(this, "Tidak bisa Memproses", Toast.LENGTH_SHORT).show()
                }
            } else {
                myRef.child(Data.MENU).child(Data.key).child("stokMakanan")
                    .setValue(jmlh.toString())
            }
        }
    }

    private fun submitMenu(tempModel: DataTemp) {
        val target: String = Data.namaPesanan
        myRef.child(Data.TEMP)
            .child(Data.idUser)
            .child(target.replace(' ', '_'))
            .setValue(tempModel)
        sheetJumPesan.state = BottomSheetBehavior.STATE_HIDDEN
        sheetPesan.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun onGetPesanan() {
        myRef.child(Data.TEMP).child(Data.idUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listPesan: ArrayList<DataTemp> = arrayListOf()
                    var hargaSatu: Int
                    var hargaTotal = 0
                    for (dataSnapshot1 in snapshot.children) {
                        val dataTemp = dataSnapshot1.getValue(DataTemp::class.java)
                        hargaSatu = dataTemp?.hargaPesanan?.toInt()!!
                        hargaTotal += hargaSatu
                        listPesan.add(dataTemp)
                    }
                    val totalRp = formatRupiah.format(hargaTotal.toDouble())
                    totalPesanan = listPesan.size.toString()
                    pesan.text = "Lihat Keranjang $totalPesanan Pesanan  $totalRp"
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        pesan.setOnClickListener {
            val i = Intent(this, KeranjangActivity::class.java)
            this.startActivity(i)
        }
    }
}