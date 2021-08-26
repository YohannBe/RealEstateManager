package com.example.realestatemanager.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import java.util.concurrent.Executor

class SettingViewModelFactory (private val realEstateAgentRepository: RealEstateAgentRepository,
                               private val executor: Executor
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingViewModel(realEstateAgentRepository, executor) as T
    }
}