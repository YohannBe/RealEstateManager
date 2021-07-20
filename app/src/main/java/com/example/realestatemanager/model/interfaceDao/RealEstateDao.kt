package com.example.realestatemanager.model.interfaceDao


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.realestatemanager.model.myObjects.RealEstate


@Dao
interface RealEstateDao {

    @Query("SELECT * FROM RealEstate WHERE id = :id")
    fun loadRealEstate(id: Int): LiveData<RealEstate>

    @Query("SELECT * FROM RealEstate")
    fun loadAllRealEstate(): LiveData<List<RealEstate>>

    @Delete
    fun deleteRealEstate(realEstate: RealEstate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRealEstate(realEstate: RealEstate)

    @Update
    fun updateRealEstate(realEstate: RealEstate)
}