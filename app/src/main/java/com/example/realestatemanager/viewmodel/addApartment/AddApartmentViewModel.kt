package com.example.realestatemanager.viewmodel.addApartment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor

class AddApartmentViewModel (
    private val realEstateRepository: RealEstateRepository,
    private val executor: Executor
        ): ViewModel() {
    private val idInserted: MutableLiveData<Long> = MutableLiveData()

    fun insertApartment(realEstate: RealEstate){
        executor.execute {
            idInserted.postValue(realEstateRepository.insertRealEstate(realEstate))
        }
    }

    fun getIdInserted(): LiveData<Long> {
        return idInserted
    }

}