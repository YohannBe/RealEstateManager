package com.example.realestatemanager.viewmodel.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor

class MainActivityViewModelFactory (private val realEstateAgentRepository: RealEstateAgentRepository,
                                    private val realEstateRepository: RealEstateRepository,
                                    private val executor: Executor
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(realEstateAgentRepository, realEstateRepository, executor) as T
    }
}