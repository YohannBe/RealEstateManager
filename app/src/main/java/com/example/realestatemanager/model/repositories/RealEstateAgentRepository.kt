package com.example.realestatemanager.model.repositories

import androidx.lifecycle.LiveData
import com.example.realestatemanager.model.interfaceDao.RealEstateAgentDao
import com.example.realestatemanager.model.myObjects.RealEstateAgent



class RealEstateAgentRepository (private val realEstateAgentDao: RealEstateAgentDao){

    fun getMyAgent(id:Int): LiveData<RealEstateAgent> {
        return realEstateAgentDao.loadRealEstateAgent(id)
    }

    fun deleteMyAgent(realEstateAgent: RealEstateAgent){
        realEstateAgentDao.deleteRealEstateAgent(realEstateAgent)
    }

    fun updateMyAgent(vararg realEstateAgent: RealEstateAgent){
        realEstateAgentDao.updateRealEstateAgent(*realEstateAgent)
    }

    fun insertAgents(vararg realEstateAgent: RealEstateAgent){
        realEstateAgentDao.insertRealEstateAgent(*realEstateAgent)
    }

    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent>{
        return realEstateAgentDao.loadRealEstateAgentAccount(mail, password)
    }

    fun findExistingMail(mail: String): LiveData<String>{
        return realEstateAgentDao.findExistingMail(mail)
    }
}




