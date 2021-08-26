package com.example.realestatemanager.viewmodel

import android.content.Context
import com.example.realestatemanager.model.mDatabase.MDataBase
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import com.example.realestatemanager.viewmodel.addApartment.AddApartmentViewModelFactory
import com.example.realestatemanager.viewmodel.detailFragment.DetailFragmentVIewModelFactory
import com.example.realestatemanager.viewmodel.login.LoginViewModelFactory
import com.example.realestatemanager.viewmodel.mainActivity.MainActivityViewModelFactory
import com.example.realestatemanager.viewmodel.profileFragment.ProfileFragmentViewModelFactory
import com.example.realestatemanager.viewmodel.profileSetting.ProfileSettingViewModelFactory
import com.example.realestatemanager.viewmodel.setting.SettingViewModelFactory

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

        fun provideViewModelFactory(context: Context): ProfileFragmentViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            return ProfileFragmentViewModelFactory(realEstateAgentRepository)
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

        fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            return LoginViewModelFactory(realEstateAgentRepository)
        }

        fun provideProfileSettingViewModelFactory(context: Context): ProfileSettingViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            val executor = providerExecutor()
            return ProfileSettingViewModelFactory(realEstateAgentRepository, executor)
        }

        fun provideSettingViewModelFactory(context: Context): SettingViewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            val executor = providerExecutor()
            return SettingViewModelFactory(realEstateAgentRepository, executor)
        }

        fun provideDetailFragmentViewModelFactory(context: Context): DetailFragmentVIewModelFactory {
            val realEstateAgentRepository = providerAgentData(context)
            val realEstateRepository = providerApartmentData(context)
            val executor = providerExecutor()
            return DetailFragmentVIewModelFactory(realEstateAgentRepository, realEstateRepository, executor)
        }
    }
}