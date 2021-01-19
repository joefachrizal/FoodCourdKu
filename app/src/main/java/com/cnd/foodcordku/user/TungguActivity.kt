package com.cnd.foodcordku.user

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cnd.foodcordku.adapter.KeranjangAdapter
import com.cnd.foodcordku.databinding.ActivityTungguBinding
import com.cnd.foodcordku.helper.UserPreferences
import com.cnd.foodcordku.model.DataTemp
import com.cnd.foodcordku.model.DataPemesan
import com.cnd.foodcordku.util.Data
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class TungguActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTungguBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var keranjangAdapter: KeranjangAdapter
    private lateinit var localeID: Locale
    private lateinit var formatRupiah: NumberFormat
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTungguBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference

        val titleText = "Ka ${Data.pemesan}"

        if (Data.takeAway == "yes") {
            binding.statusTake.text = "$titleText Take Away"
        } else {
            binding.statusTake.text = "$titleText Di Meja ${Data.ID}"
        }

        initData()
        onGetData()
    }

    private fun initData() {
        localeID = Locale("in", "ID")
        formatRupiah = NumberFormat.getCurrencyInstance(localeID)

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
                        Data.waktu = KeranjangActivity.TIME.toString()
                    } else if (listPesan.size > 1) {
                        Data.waktu = (KeranjangActivity.TIME + 5 * listPesan.size).toString()
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

        onTakeBuy()
    }

    private fun onTakeBuy() {
        myRef.child(Data.KONFIRMASI).child(Data.idUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataTemp = snapshot.getValue(DataPemesan::class.java)
                    val timer = dataTemp?.waktu?.toInt() ?: 0
                    val status = dataTemp?.statusPesanan
                    Data.statusPesanan = status.toString()

                    if (status == "ok") {
                        binding.textNotif.text = "Pesanan Sedang Di buat"
                        binding.textViewCountdown.visibility = View.VISIBLE
                        binding.konfirmasiBayar.visibility = View.GONE
                        binding.lavNotification.setAnimation("lottie-anim/cooking.json")
                        binding.lavNotification.repeatCount = ValueAnimator.INFINITE
                        binding.lavNotification.playAnimation()

                        onCountDownTimer(1.toLong())
//                        onCountDownTimer(timer.toLong())
                    } else {
                        binding.textNotif.text = "Sedang Menunggu Antrian"
                        binding.konfirmasiBayar.visibility = View.GONE
                        binding.lavNotification.setAnimation("lottie-anim/artishow.json")
                        binding.lavNotification.repeatCount = ValueAnimator.INFINITE
                        binding.lavNotification.playAnimation()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun onCountDownTimer(timer: Long) {
        var timeValue = timer
        countDownTimer = object : CountDownTimer(timeValue * 60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(timeFinished: Long) {
                timeValue = timeFinished

                val hours = (timeValue / 1000).toInt() / 3600
                val minutes = (timeValue / 1000 % 3600).toInt() / 60
                val seconds = (timeValue / 1000).toInt() % 60

                val timeLeftFormatted: String
                timeLeftFormatted = if (hours > 0) {
                    String.format(
                        Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds
                    )
                } else {
                    String.format(
                        Locale.getDefault(), "%02d:%02d", minutes, seconds
                    )
                }
                binding.textViewCountdown.text = timeLeftFormatted
            }

            override fun onFinish() {
                binding.textNotif.text =
                    "Pesanan Sudah Siap\nHarap Siapkan Uang Pas\ndan tetep jaga jarak"
                binding.textViewCountdown.visibility = View.GONE
                binding.konfirmasiBayar.visibility = View.VISIBLE
                binding.lavNotification.setAnimation("lottie-anim/cooker-remix.json")
                binding.lavNotification.repeatCount = ValueAnimator.INFINITE
                binding.lavNotification.playAnimation()

                onDoneBuy()
            }
        }.start()
    }

    private fun onDoneBuy() {
        binding.konfirmasiBayar.setOnClickListener {
            myRef.child(Data.KONFIRMASI).child(Data.idUser).child("statusBayar").setValue("lunas")
//            myRef.child(Data.TEMP).child(Data.idUser).removeValue().addOnSuccessListener {
                Data.idUser = ""
                Data.ID = ""
                Data.namaPesanan = ""
                Data.jumlahPesanan = ""
                Data.hargaPesanan = ""
                Data.statusPesanan = ""
                Data.pemesan = ""
                Data.hargaBayar = ""
                Data.banyakPesanan = ""

                lifecycleScope.launch {
                    userPreferences.saveIdUser(Data.idUser)
                }

                val intent = Intent(this, PilihMejaActivity::class.java)
                startActivity(intent)
                finishAffinity()
//            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Peringatan!")
            .setMessage("Apakah anda akan keluar?")

            .setNegativeButton("Batal") { _, _ ->
                null
            }

            .setPositiveButton("Ya") { _, _ ->
                val intent = Intent(this, PilihMejaActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            .show()
    }
}