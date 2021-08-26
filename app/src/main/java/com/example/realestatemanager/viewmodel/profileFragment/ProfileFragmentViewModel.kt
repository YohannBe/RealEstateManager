package com.example.realestatemanager.viewmodel.profileFragment


import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor


class ProfileFragmentViewModel(
    private val realEstateAgentRepository: RealEstateAgentRepository,
) : ViewModel() {

    fun getMyAgent(id: Int): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.getMyAgent(id)
    }
}