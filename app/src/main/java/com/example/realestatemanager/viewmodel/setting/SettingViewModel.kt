package com.example.realestatemanager.viewmodel.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import java.util.concurrent.Executor

class SettingViewModel (
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val executor: Executor
) : ViewModel() {

    fun getMyAgent(id: Int): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.getMyAgent(id)
    }

    fun updateMyAgent(vararg realEstateAgent: RealEstateAgent) {
        executor.execute {
            realEstateAgentRepository.updateMyAgent(*realEstateAgent)
        }
    }

}