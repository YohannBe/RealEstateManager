package com.example.realestatemanager.model.repositories

import androidx.lifecycle.LiveData
import com.example.realestatemanager.model.interfaceDao.RealEstateDao
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent

class RealEstateRepository (private val realEstateDao: RealEstateDao) {

    fun loadAllRealEstate(): LiveData<List<RealEstate>>{
        return realEstateDao.loadAllRealEstate()
    }

    fun insertRealEstate(realEstate: RealEstate){
        realEstateDao.insertRealEstate(realEstate)
    }

    fun updateRealEstate(realEstate: RealEstate){
        realEstateDao.updateRealEstate(realEstate)
    }

    fun deleteRealEstate(realEstate: RealEstate){
        realEstateDao.deleteRealEstate(realEstate)
    }


}