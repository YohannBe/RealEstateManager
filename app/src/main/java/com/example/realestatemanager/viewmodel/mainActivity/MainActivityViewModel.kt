package com.example.realestatemanager.viewmodel.mainActivity

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import com.example.realestatemanager.utils.compareDateAfter
import com.example.realestatemanager.utils.compareDateBefore
import java.util.concurrent.Executor

class MainActivityViewModel(
    private val realEstateAgentRepository: RealEstateAgentRepository,
    private val realEstateRepository: RealEstateRepository,
    private val executor: Executor
) : ViewModel() {

    private var filterLiveData: MutableLiveData<Bundle> = MutableLiveData()
    private var currentAgentId: MutableLiveData<Int> = MutableLiveData()

    fun getAllApartment(): LiveData<List<RealEstate>> {
        return realEstateRepository.loadAllRealEstate()
    }

    fun loadAllMyRealEstate(id: Int): LiveData<List<RealEstate>> {
        return realEstateRepository.loadAllMyRealEstate(id)
    }

    fun deleteRealEstate(realEstate: RealEstate) {
        executor.execute {
            realEstateRepository.deleteRealEstate(realEstate)
        }
    }

    fun getMyAgent(id: Int): LiveData<RealEstateAgent> {
        return realEstateAgentRepository.getMyAgent(id)
    }

    fun getFilter(): LiveData<Bundle> {
        return filterLiveData
    }

    fun getIdCurrentAgent(): LiveData<Int> {
        return currentAgentId
    }

    fun setIdCurrentAgent(id: Int) {
        currentAgentId.postValue(id)
    }

    fun passFilter(
        filter: Boolean,
        listFilter: HashMap<String, String>?,
        priceFrom: Int,
        priceTo: Int,
        surfaceFrom: Int,
        surfaceTo: Int,
        chosenCity: String,
        beforeText: String,
        afterText: String,
        betweenTextFirst: String,
        betweenTextSecond: String,
    ) {
        val bundle = Bundle()
        bundle.putBoolean("filterPut", filter)
        bundle.putSerializable("mapFilter", listFilter)
        bundle.putInt("priceFrom", priceFrom)
        bundle.putInt("priceTo", priceTo)
        bundle.putInt("surfaceFrom", surfaceFrom)
        bundle.putInt("surfaceTo", surfaceTo)
        bundle.putString("chosenCity", chosenCity)
        bundle.putString("beforeText", beforeText)
        bundle.putString("afterText", afterText)
        bundle.putString("betweenTextFirst", betweenTextFirst)
        bundle.putString("betweenTextSecond", betweenTextSecond)
        filterLiveData.postValue(bundle)
    }

    fun updateListFilter(
        bundle: Bundle,
        listRealEstate: List<RealEstate>
    ): List<RealEstate> {
        if (bundle.getBoolean("filterPut")) {
            val listFilter: HashMap<String, String> =
                bundle.getSerializable("mapFilter") as HashMap<String, String>
            val priceFrom: Int = bundle.getInt("priceFrom")
            val priceTo: Int = bundle.getInt("priceTo")
            val surfaceFrom: Int = bundle.getInt("surfaceFrom")
            val surfaceTo: Int = bundle.getInt("surfaceTo")
            val chosenCity: String = bundle.getString("chosenCity")!!
            val beforeText: String = bundle.getString("beforeText")!!
            val afterText: String = bundle.getString("afterText")!!
            val betweenTextFirst: String = bundle.getString("betweenTextFirst")!!
            val betweenTextSecond: String = bundle.getString("betweenTextSecond")!!
            val filteredList = ArrayList<RealEstate>()
            var type: Boolean
            var room: Boolean
            var bedroom: Boolean
            var bathroom: Boolean
            var price: Boolean
            var surface: Boolean
            var poi: Boolean
            var numberPic: Boolean
            var city: Boolean
            var beforeDate: Boolean
            var afterDate: Boolean
            var between: Boolean
            for (i in listRealEstate.indices) {
                type = false
                poi = false
                numberPic = false

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


                if (!listFilter.containsKey("chipSchool") && !listFilter.containsKey("chipPark") && !listFilter.containsKey(
                        "chipSubway"
                    ) && !listFilter.containsKey("chipRestaurant") && !listFilter.containsKey("chipSport") && !listFilter.containsKey(
                        "chipStadium"
                    ) && !listFilter.containsKey("chipPool") && !listFilter.containsKey("chipBus") && !listFilter.containsKey(
                        "chipMarket"
                    )
                )
                    poi = true
                else {
                    var listIndice = 0
                    while (!poi && listIndice < listRealEstate[i].listPOI.size) {
                        when (listRealEstate[i].listPOI[listIndice]) {
                            "school" -> if (listFilter["chipSchool"] != null) poi = true
                            "commerce" -> if (listFilter["chipMarket"] != null) poi = true
                            "parc" -> if (listFilter["chipPark"] != null) poi = true
                            "Bus" -> if (listFilter["chipBus"] != null) poi = true
                            "Metro" -> if (listFilter["chipSubway"] != null) poi = true
                            "restaurant" -> if (listFilter["chipRestaurant"] != null) poi = true
                            "Sport club" -> if (listFilter["chipSport"] != null) poi = true
                            "Pool" -> if (listFilter["chipPool"] != null) poi = true
                            "Stadium" -> if (listFilter["chipStadium"] != null) poi = true
                        }
                        listIndice++
                    }
                }



                if (!listFilter.containsKey("chipPic1") && !listFilter.containsKey("chipPic2") && !listFilter.containsKey(
                        "chipPic3"
                    )
                )
                    numberPic = true
                else {

                    if (listFilter.containsKey("chipPic1") && listRealEstate[i].photoReference.size >= 1) {
                        numberPic = true
                    }
                    if (listFilter.containsKey("chipPic2") && listRealEstate[i].photoReference.size >= 2) {
                        numberPic = true
                    }
                    if (listFilter.containsKey("chipPic3") && listRealEstate[i].photoReference.size >= 3) {
                        numberPic = true
                    }
                }

                price = listRealEstate[i].price in priceFrom..priceTo

                surface = listRealEstate[i].surface in surfaceFrom..surfaceTo

                city =
                    chosenCity == "" || chosenCity == "Select a city" || listRealEstate[i].city == chosenCity

                beforeDate = if (beforeText == "") true else compareDateBefore(
                    beforeText,
                    listRealEstate[i].dateStart
                )
                afterDate = if (afterText == "") true else compareDateAfter(
                    afterText,
                    listRealEstate[i].dateStart
                )

                between = if (betweenTextFirst == "" && betweenTextSecond == "") true
                else compareDateBefore(betweenTextSecond, listRealEstate[i].dateStart)
                        && compareDateAfter(betweenTextFirst, listRealEstate[i].dateStart)

                if (type && room && bedroom && price && surface && bathroom && poi && numberPic && city && beforeDate && afterDate && between) {
                    filteredList.add(listRealEstate[i])
                }
            }

            return filteredList
        } else return listRealEstate
    }
}