package com.tapisdev.penjualankasir.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.penjualankasir.activity.SelectPelangganActivity
import com.tapisdev.penjualankasir.databinding.ItemsPelangganBinding
import com.tapisdev.penjualankasir.databinding.ItemsTransaksiBinding
import com.tapisdev.penjualankasir.model.Hutang
import com.tapisdev.penjualankasir.model.Pelanggan
import com.tapisdev.penjualankasir.model.Transaksi
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterHutang(private val list:ArrayList<Hutang>) : RecyclerView.Adapter<AdapterHutang.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = ItemsTransaksiBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = list?.size


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    inner class MyViewHolder(val binding: ItemsTransaksiBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(list.get(position)){
                val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
                val df = nf as DecimalFormat

                var nama_pelanggan = ""
                if (list?.get(position).nama_pelanggan == null || list?.get(position).nama_pelanggan.equals("")){
                    nama_pelanggan = "Guest"
                }
                else{
                    nama_pelanggan = list?.get(position).nama_pelanggan!!
                }

                if (list?.get(position).hutang_type.equals("saya")){
                    binding.tvNamaPelanggan.setText(list?.get(position).deskripsi)
                }else{
                    binding.tvNamaPelanggan.setText(nama_pelanggan)
                }
                binding.tvTanggal.setText(""+list?.get(position).tgl_hutang)
                binding.tvTotalBayar.setText("Hutang Rp. "+df.format(list?.get(position).hutang))

                binding.rlBarang.setOnClickListener {


                   /* val i  = Intent(binding.rlPelanggan.context,DetailBarangActivity::class.java)
                    i.putExtra("pelanggan",list?.get(position))
                    binding.rlPelanggan.context.startActivity(i)*/
                }

            }
        }

    }

}