package com.example.realestatemanager.viewmodel

import android.content.Context
import com.example.realestatemanager.model.mDatabase.MDataBase
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import com.example.realestatemanager.viewmodel.addApartment.AddApartmentViewModelFactory
import com.example.realestatemanager.viewmodel.mainActivity.MainActivityViewModelFactory

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

        fun provideAddApartmentViewModel(context: Context): AddApartmentViewModelFactory {
            val realEstateRepository = providerApartmentData(context)
            val executor = providerExecutor()
            return AddApartmentViewModelFactory(realEstateRepository, executor)
        }

        fun provideMainActivityViewModelFactory(context: Context): MainActivityViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            val realEstateRepository = providerApartmentData(context)
            val executor = providerExecutor()
            return MainActivityViewModelFactory(realEstateAgentRepository, realEstateRepository, executor)
        }
    }
}