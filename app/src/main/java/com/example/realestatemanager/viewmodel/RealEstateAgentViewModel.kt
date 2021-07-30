package com.example.realestatemanager.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor


class RealEstateAgentViewModel(
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val realEstateRepository: RealEstateRepository,
    private val executor: Executor
) : ViewModel() {

    fun insertAgent(realEstateAgent: RealEstateAgent) {
        executor.execute {
            realEstateAgentRepository.insertAgents(realEstateAgent)
        }
    }

    fun getMyAgent(id: Int): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.getMyAgent(id)
    }

    fun deleteMyAgent(realEstateAgent: RealEstateAgent) {
        realEstateAgentRepository.deleteMyAgent(realEstateAgent)
    }

    fun updateMyAgent(vararg realEstateAgent: RealEstateAgent) {
        realEstateAgentRepository.updateMyAgent(*realEstateAgent)
    }

    fun getAllApartment(): LiveData<List<RealEstate>> {
        return realEstateRepository.loadAllRealEstate()
    }

    fun insertApartment(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.insertRealEstate(realEstate)
        }
    }

    fun deleteRealEstate(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.deleteRealEstate(realEstate)
        }
    }

    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.loadRealEstateAgentAccount(mail, password)
    }

    fun findExistingMail(mail: String): LiveData<String> {
        return realEstateAgentRepository.findExistingMail(mail)
    }

    fun loadRealEstate(id: Int): LiveData<RealEstate>{
        return realEstateRepository.loadRealEstate(id)
    }

    fun updateRealEstate(realEstate: RealEstate){
        executor.execute{
            realEstateRepository.updateRealEstate(realEstate)
        }
    }

}