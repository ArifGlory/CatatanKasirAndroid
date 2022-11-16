package com.tapisdev.penjualankasir.adapter


import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.penjualankasir.activity.DetailHutangActivity
import com.tapisdev.penjualankasir.activity.SelectPelangganActivity
import com.tapisdev.penjualankasir.databinding.ItemsCicilanBinding
import com.tapisdev.penjualankasir.databinding.ItemsHutangBinding
import com.tapisdev.penjualankasir.databinding.ItemsPelangganBinding
import com.tapisdev.penjualankasir.databinding.ItemsTransaksiBinding
import com.tapisdev.penjualankasir.model.CicilanHutang
import com.tapisdev.penjualankasir.model.Hutang
import com.tapisdev.penjualankasir.model.Pelanggan
import com.tapisdev.penjualankasir.model.Transaksi
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterCicilanHutang(private val list:ArrayList<CicilanHutang>) : RecyclerView.Adapter<AdapterCicilanHutang.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ItemsCicilanBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = list?.size


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    inner class MyViewHolder(val binding: ItemsCicilanBinding)
        :RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(list.get(position)){
                val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
                val df = nf as DecimalFormat


                binding.tvCicilan.setText("Rp. "+df.format(list.get(position).jml_cicilan))
                binding.tvTanggal.setText(list?.get(position).tanggal)


            }
        }

    }

}