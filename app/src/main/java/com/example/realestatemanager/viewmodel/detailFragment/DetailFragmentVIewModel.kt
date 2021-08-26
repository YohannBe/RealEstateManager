package com.example.realestatemanager.viewmodel.detailFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor

class DetailFragmentVIewModel(
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val realEstateRepository: RealEstateRepository,
    private val executor: Executor
) : ViewModel() {

    fun loadRealEstate(id: Int): LiveData<RealEstate> {
        return realEstateRepository.loadRealEstate(id)
    }

    fun updateRealEstate(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.updateRealEstate(realEstate)
        }
    }
}