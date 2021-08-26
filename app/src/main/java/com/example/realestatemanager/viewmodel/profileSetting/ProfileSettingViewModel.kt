package com.example.realestatemanager.viewmodel.profileSetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import java.util.concurrent.Executor

class ProfileSettingViewModel  (
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val executor: Executor
) : ViewModel() {

    fun insertAgent(realEstateAgent: RealEstateAgent) {
        executor.execute {
            realEstateAgentRepository.insertAgents(realEstateAgent)
        }
    }

    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.loadRealEstateAgentAccount(mail, password)
    }
}