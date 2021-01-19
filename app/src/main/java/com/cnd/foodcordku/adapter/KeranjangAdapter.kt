package com.cnd.foodcordku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cnd.foodcordku.databinding.ItemKeranjangBinding
import com.cnd.foodcordku.model.DataTemp
import java.text.NumberFormat
import java.util.*

class KeranjangAdapter(private var dataList: ArrayList<DataTemp>) :
    RecyclerView.Adapter<KeranjangAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemKeranjangBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    inner class ViewHolder(var binding: ItemKeranjangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataTemp) {

            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

            with(binding) {
                namaSatuan.text = item.namaPesanan
                hargaSatuan.text = formatRupiah.format(item.hargaPesanan?.toDouble())
                jumlahSatuan.text = item.jumlahPesanan
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    init {
        notifyDataSetChanged()
    }
}