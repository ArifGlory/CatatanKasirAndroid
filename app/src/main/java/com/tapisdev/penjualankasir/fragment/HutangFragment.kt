package com.tapisdev.penjualankasir.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.tapisdev.penjualankasir.activity.TambahBarangActivity
import com.tapisdev.penjualankasir.databinding.FragmentHomeBinding
import com.tapisdev.penjualankasir.databinding.FragmentHutangBinding
import com.tapisdev.penjualankasir.databinding.FragmentStokBinding

class HutangFragment : Fragment() {

    private var _binding: FragmentHutangBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    //lateinit var binding_shimmer : ShimmerSuratBinding
    lateinit var shimmerFrameLayout : ShimmerFrameLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHutangBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnTambahHutang.setOnClickListener {

        }



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): HutangFragment{
            val fragment = HutangFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}