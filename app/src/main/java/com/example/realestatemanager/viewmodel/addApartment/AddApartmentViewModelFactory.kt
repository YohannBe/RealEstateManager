package com.example.realestatemanager.viewmodel.addApartment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor

class AddApartmentViewModelFactory (private val realEstateRepository: RealEstateRepository,
                                    private val executor: Executor
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddApartmentViewModel(realEstateRepository, executor) as T
    }
}