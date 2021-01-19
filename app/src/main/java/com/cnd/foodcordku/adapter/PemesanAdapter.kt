package com.cnd.foodcordku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cnd.foodcordku.R
import com.cnd.foodcordku.databinding.ItemDipesanBinding
import com.cnd.foodcordku.model.DataPemesan
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class PemesanAdapter(private var dataList: ArrayList<DataPemesan>) :
    RecyclerView.Adapter<PemesanAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemDipesanBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    inner class ViewHolder(var binding: ItemDipesanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataPemesan) {

            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

            with(binding) {
                namaPemesan.text =item.pemesan
                hargaMakanan.text = formatRupiah.format(item.hargaBayar?.toDouble())
                banyakPesanan.text =item.banyakPesanan
                statusPesanan.text =item.statusPesanan
                statusBayar.text =item.statusBayar

                switchPesan.isOn = item.statusPesanan != "pending"

                switchPesan.setOnToggledListener { _, isOn ->
                    if (isOn){
                        Toast.makeText(itemView.context, "ok", Toast.LENGTH_SHORT).show()
                        onItemClickCallback.onSlide(item,"ok")
                    } else{
                        Toast.makeText(itemView.context, "tidak ok", Toast.LENGTH_SHORT).show()
                        onItemClickCallback.onSlide(item,"pending")
                    }
                }

                if (item.statusBayar == "lunas"){
                    okStatus.setBackgroundResource(R.color.textColor)
                }
                itemView.setOnClickListener {
                    onItemClickCallback.onClicked(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    init {
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onClicked(data: DataPemesan)
        fun onSlide(data: DataPemesan, konfirmasi: String)
    }
}