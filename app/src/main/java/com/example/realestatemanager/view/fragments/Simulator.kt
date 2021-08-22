package com.example.realestatemanager.view.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.example.realestatemanager.databinding.FragmentSimulatorBinding


class Simulator : Fragment() {

    private lateinit var binding: FragmentSimulatorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimulatorBinding.inflate(layoutInflater)
        initElements()
        return binding.root
    }

    private fun initElements() {

        binding.buttonSimulator.setOnClickListener {
            if (!TextUtils.isEmpty(binding.percentEdittextSimulator.text) && !TextUtils.isEmpty(
                    binding.takingEdittextSimulator.text
                ) &&
                !TextUtils.isEmpty(binding.yearEdittextSimulator.text)
            ) {
                val result = calculSimulator(
                    binding.percentEdittextSimulator.text.toString().toFloat(),
                    binding.takingEdittextSimulator.text.toString().toInt(),
                    binding.yearEdittextSimulator.text.toString().toInt()
                )
                buildSimulatorDialog(result)
            }
        }
    }

    private fun calculSimulator(percent: Float, taking: Int, year: Int): String {
        val result = (taking + taking * (percent / 100)) / year
        return result.toString()
    }

    private fun buildSimulatorDialog(result: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("After calcul")
            .setMessage("$result/year")
            .setPositiveButton("Ok") { dialog, i ->
                dialog.dismiss()
            }
        dialogBuilder.show()


    }


}