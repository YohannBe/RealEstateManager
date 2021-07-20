package com.example.realestatemanager.model.interfaceDao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.realestatemanager.model.myObjects.RealEstateAgent

@Dao
interface RealEstateAgentDao {

    @Query("SELECT * FROM RealEstateAgent WHERE id = :id")
    fun loadRealEstateAgent(id: Int): LiveData<RealEstateAgent>

    @Query("SELECT * FROM RealEstateAgent WHERE mail = :mail AND password = :password")
    fun loadRealEstateAgentAccount(mail: String, password: String): LiveData<RealEstateAgent>

    @Query("SELECT mail FROM RealEstateAgent WHERE mail = :mail")
    fun findExistingMail(mail: String): LiveData<String>

    @Delete
    fun deleteRealEstateAgent(realEstateAgent: RealEstateAgent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRealEstateAgent(vararg realEstateAgent: RealEstateAgent)

    @Update
    fun updateRealEstateAgent(vararg realEstateAgent: RealEstateAgent)
}