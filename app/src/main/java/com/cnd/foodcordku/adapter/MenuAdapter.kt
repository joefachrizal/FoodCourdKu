package com.cnd.foodcordku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cnd.foodcordku.databinding.ItemMenuMakananBinding
import com.cnd.foodcordku.model.DataMenu
import java.text.NumberFormat
import java.util.*

class MenuAdapter(private var dataList: ArrayList<DataMenu>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>(), Filterable {
    val userListFull: ArrayList<DataMenu> = arrayListOf()

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = ItemMenuMakananBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(var binding: ItemMenuMakananBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(dataMenu: DataMenu) {

            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

            with(binding) {
                judulMakanan.text = dataMenu.titleFood
                namaOtlte.text = dataMenu.otlet
                deskripsiMakanan.text = dataMenu.description
                stokMakanan.text = dataMenu.stokMakanan
                hargaMakanan.text = formatRupiah.format(dataMenu.price?.toDouble())
                Glide.with(itemView.context)
                    .load(dataMenu.urlImage)
                    .into(fotoMakanan)
            }

            itemView.setOnClickListener {
                onItemClickCallback.onClicked(dataMenu)
            }
        }
    }

    interface OnItemClickCallback {
        fun onClicked(data: DataMenu)
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    private var myFilter: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<DataMenu> = arrayListOf()
            if (charSequence.isEmpty()) {
                filteredList.addAll(userListFull)
            } else {
                for (data in userListFull) {
                    if (data.titleFood?.toLowerCase(Locale.getDefault())
                            ?.contains(charSequence.toString().toLowerCase(Locale.getDefault()))!!
                    ) {
                        filteredList.add(data)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //Automatic on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            dataList.clear()
            dataList.addAll((filterResults.values as Collection<DataMenu>))
            notifyDataSetChanged()
        }
    }

    init {
        userListFull.addAll(dataList)
    }
}