package com.example.realestatemanager.viewmodel


import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor


class RealEstateAgentViewModel(
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val realEstateRepository: RealEstateRepository,
    private val executor: Executor
) : ViewModel() {

    fun insertAgent(realEstateAgent: RealEstateAgent) {
        executor.execute {
            realEstateAgentRepository.insertAgents(realEstateAgent)
        }
    }

    fun getMyAgent(id: Int): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.getMyAgent(id)
    }

    fun deleteMyAgent(realEstateAgent: RealEstateAgent) {
        realEstateAgentRepository.deleteMyAgent(realEstateAgent)
    }

    fun updateMyAgent(vararg realEstateAgent: RealEstateAgent) {
        executor.execute {
            realEstateAgentRepository.updateMyAgent(*realEstateAgent)
        }
    }

    fun getAllApartment(): LiveData<List<RealEstate>> {
        return realEstateRepository.loadAllRealEstate()
    }

    fun deleteRealEstate(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.deleteRealEstate(realEstate)
        }
    }

    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.loadRealEstateAgentAccount(mail, password)
    }

    fun findExistingMail(mail: String): LiveData<String> {
        return realEstateAgentRepository.findExistingMail(mail)
    }

    fun loadRealEstate(id: Int): LiveData<RealEstate> {
        return realEstateRepository.loadRealEstate(id)
    }

    fun updateRealEstate(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.updateRealEstate(realEstate)
        }
    }

    private var filterLiveData: MutableLiveData<Bundle> = MutableLiveData()

    fun getFilter(): LiveData<Bundle> {
        return filterLiveData
    }


    fun updateListFilter(
        bundle: Bundle,
        listRealEstate: List<RealEstate>
    ): List<RealEstate> {
        if(bundle.getBoolean("filterPut")) {
            val listFilter: HashMap<String, String> =
                bundle.getSerializable("mapFilter") as HashMap<String, String>
            val priceFrom: Int = bundle.getInt("priceFrom")
            val priceTo: Int = bundle.getInt("priceTo")
            val surfaceFrom: Int = bundle.getInt("surfaceFrom")
            val surfaceTo: Int = bundle.getInt("surfaceTo")
            val filteredList = ArrayList<RealEstate>()
            var type: Boolean
            var room: Boolean
            var bedroom: Boolean
            var bathroom: Boolean
            var price: Boolean
            var surface: Boolean
            for (i in listRealEstate.indices) {
                type = false

                if (!listFilter.containsKey("appartment") && !listFilter.containsKey("loft") && !listFilter.containsKey(
                        "duplex"
                    ) && !listFilter.containsKey("villa")
                )
                    type = true
                else
                    when (listRealEstate[i].type) {
                        "Apartment" -> type = listFilter["appartment"] != null
                        "Loft" -> type = listFilter["loft"] != null
                        "Duplex" -> type = listFilter["duplex"] != null
                        "Studio" -> type = listFilter["villa"] != null
                    }

                room =
                    if (!listFilter.containsKey("room1") && !listFilter.containsKey("room2") && !listFilter.containsKey(
                            "room3"
                        ) && !listFilter.containsKey("room4") && !listFilter.containsKey("room5")
                    )
                        true
                    else
                        when (listRealEstate[i].roomNumber) {
                            1 -> listFilter["room1"] != null
                            2 -> listFilter["room2"] != null
                            3 -> listFilter["room3"] != null
                            4 -> listFilter["room4"] != null
                            5 -> listFilter["room5"] != null
                            else -> true
                        }

                bedroom =
                    if (!listFilter.containsKey("bedroom1") && !listFilter.containsKey("bedroom2") && !listFilter.containsKey(
                            "bedroom3"
                        ) && !listFilter.containsKey("bedroom4") && !listFilter.containsKey("bedroom5")
                    )
                        true
                    else
                        when (listRealEstate[i].numberBedroom) {
                            1 -> listFilter["bedroom1"] != null
                            2 -> listFilter["bedroom2"] != null
                            3 -> listFilter["bedroom3"] != null
                            4 -> listFilter["bedroom4"] != null
                            5 -> listFilter["bedroom5"] != null
                            else -> true
                        }

                bathroom =
                    if (!listFilter.containsKey("bathroom1") && !listFilter.containsKey("bathroom2") && !listFilter.containsKey(
                            "bathroom3"
                        )
                    )
                        true
                    else
                        when (listRealEstate[i].numberBathroom) {
                            1 -> listFilter["bathroom1"] != null
                            2 -> listFilter["bathroom2"] != null
                            3 -> listFilter["bathroom3"] != null
                            else -> true
                        }



                price = listRealEstate[i].price in priceFrom..priceTo

                surface = listRealEstate[i].surface in surfaceFrom..surfaceTo

                if (type && room && bedroom && price && surface && bathroom) {
                    filteredList.add(listRealEstate[i])
                }
            }

            return filteredList
        }
        else return listRealEstate
    }
}