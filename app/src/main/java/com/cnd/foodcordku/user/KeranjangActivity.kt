package com.cnd.foodcordku.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.cnd.foodcordku.adapter.KeranjangAdapter
import com.cnd.foodcordku.databinding.ActivityKeranjangBinding
import com.cnd.foodcordku.helper.UserPreferences
import com.cnd.foodcordku.model.DataTemp
import com.cnd.foodcordku.model.DataPemesan
import com.cnd.foodcordku.util.Data
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.sheet_nama_pemesan.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class KeranjangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKeranjangBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var userPreferences: UserPreferences
    private lateinit var keranjangAdapter: KeranjangAdapter
    private lateinit var localeID: Locale
    private lateinit var formatRupiah: NumberFormat

    private lateinit var sheetNamaPemesan: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val TIME = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val titleText = "Meja ${Data.ID}"
        if (Data.takeAway == "yes") {
            binding.statusTake.text = "Take Away"
        } else {
            binding.statusTake.text = titleText
        }

        initData()

        if (Data.akses != "admin") {
            onShowTime()
        } else{
            binding.konfirmasiPesan.text = "Kembali"
        }

        onGetData()
    }

    private fun initData() {
        localeID = Locale("in", "ID")
        formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        sheetNamaPemesan = BottomSheetBehavior.from(sheet_nama_pemesan)
        sheetNamaPemesan.state = BottomSheetBehavior.STATE_HIDDEN

        with(binding) {
            listPesanan.adapter?.notifyDataSetChanged()
            listPesanan.scheduleLayoutAnimation()
            listPesanan.setHasFixedSize(true)
        }
    }

    private fun onGetData() {
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
                    val totalPesanan = listPesan.size.toString()

                    Data.banyakPesanan = totalPesanan
                    Data.hargaBayar = hargaTotal.toString()

                    if (listPesan.size == 1) {
                        Data.waktu = TIME.toString()
                    } else if (listPesan.size > 1) {
                        Data.waktu = (TIME + 5 * listPesan.size).toString()
                    }

                    binding.totalHarga.text = "Total $totalPesanan Pesanan  $totalRp"
                    onShowData(listPesan)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun onShowData(listPesan: ArrayList<DataTemp>) {
        keranjangAdapter = KeranjangAdapter(listPesan)
        binding.listPesanan.adapter = keranjangAdapter

        binding.konfirmasiPesan.setOnClickListener {
            if (Data.akses != "admin") {
                val state =
                    if (sheetNamaPemesan.state != BottomSheetBehavior.STATE_EXPANDED)
                        BottomSheetBehavior.STATE_EXPANDED
                    else
                        BottomSheetBehavior.STATE_HIDDEN
                sheetNamaPemesan.state = state
            } else{
                finish()
            }
        }

        btn_exit.setOnClickListener {
            sheetNamaPemesan.state = BottomSheetBehavior.STATE_HIDDEN
        }

        btn_pesan.setOnClickListener {
            lifecycleScope.launch {
                userPreferences.saveIdUser(Data.idUser)
            }

            val namaPemesan: String = nama_pelanggan.text.toString()
            if (namaPemesan.isEmpty()) {
                nama_pelanggan.error = "Di Isi dulu kaka"
            } else {
                Data.statusPesanan = "pending"
                Data.statusBayar = "no"
                Data.pemesan = namaPemesan
                submitMenu(
                    DataPemesan(
                        Data.statusPesanan,
                        Data.pemesan,
                        Data.hargaBayar,
                        Data.banyakPesanan,
                        Data.idUser,
                        Data.waktu,
                        Data.tanggal,
                        Data.jam,
                        Data.ID,
                        Data.takeAway,
                        Data.statusBayar,
                    )
                )
            }
        }
    }

    private fun submitMenu(dataPemesan: DataPemesan) {
        myRef.child(Data.KONFIRMASI)
            .child(Data.idUser)
            .setValue(dataPemesan)

        val intent = Intent(this, TungguActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onShowTime() {
        val thread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000)
                        runOnUiThread {
                            val date = System.currentTimeMillis()
                            val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
                            val jam = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val dateString = sdf.format(date)
                            val timeString = jam.format(date)
                            Data.tanggal = dateString
                            Data.jam = timeString

                            val tglformat = Data.tanggal
                            tglformat.replace(' ', '_')
                        }
                    }
                } catch (e: InterruptedException) {
                }
            }
        }
        thread.start()
    }
}