package com.example.realestatemanager.viewmodel

import android.content.Context
import com.example.realestatemanager.model.mDatabase.MDataBase
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Injection {

    companion object {
        private fun providerAgentData (context:Context): RealEstateAgentRepository {
            val database:MDataBase = MDataBase.getDatabase(context)
            return RealEstateAgentRepository(database.realEstateAgentDao())
        }

        private fun providerApartmentData (context:Context): RealEstateRepository {
            val database:MDataBase = MDataBase.getDatabase(context)
            return RealEstateRepository(database.realEstateDao())
        }

        private fun providerExecutor (): Executor{
            return Executors.newSingleThreadExecutor()
        }

        fun provideViewModelFactory(context: Context): RealEstateAgentViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            val realEstateRepository = providerApartmentData(context)
            val executor = providerExecutor()
            return RealEstateAgentViewModelFactory(realEstateAgentRepository, realEstateRepository, executor)
        }
    }
}