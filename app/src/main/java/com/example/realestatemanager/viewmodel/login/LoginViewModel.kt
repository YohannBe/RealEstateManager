package com.example.realestatemanager.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository


class LoginViewModel (
    private val realEstateAgentRepository: RealEstateAgentRepository) : ViewModel()  {

    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.loadRealEstateAgentAccount(mail, password)
    }

    fun findExistingMail(mail: String): LiveData<String> {
        return realEstateAgentRepository.findExistingMail(mail)
    }

}