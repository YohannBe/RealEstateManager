package com.example.realestatemanager.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentDetailsBinding
import com.example.realestatemanager.databinding.FragmentProfileBinding
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.view.activities.SettingActivity
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var idRealEstateAgentRetrieved = -1
    private lateinit var mContext: Context
    private lateinit var realEstateViewModel: RealEstateAgentViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        if (arguments?.getInt(idRealEstate) != null) {
            idRealEstateAgentRetrieved = arguments?.getInt("idRealEstateAgent")!!
        } else Toast.makeText(mContext, "nope", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (idRealEstateAgentRetrieved != -1)
            initElements()


    }

    private fun initElements() {
        realEstateViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideViewModelFactory(it)).get(
                RealEstateAgentViewModel::class.java
            )
        }!!
        realEstateViewModel.getMyAgent(idRealEstateAgentRetrieved).observe(requireActivity(), Observer {
            binding.firstnametextview.text = it.firstName
            binding.lastnametextview.text = it.lastName
            binding.mailtextview.text = it.mail
            binding.buttonModifyProfile.setOnClickListener{
                val intent = Intent(requireActivity(), SettingActivity::class.java)
                intent.putExtra("idRealEstateAgent", idRealEstateAgentRetrieved)
                startActivity(intent)
            }
        })
    }
}