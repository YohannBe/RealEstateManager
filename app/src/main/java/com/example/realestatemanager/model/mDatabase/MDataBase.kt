package com.example.realestatemanager.model.mDatabase

import android.content.ContentValues
import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.realestatemanager.model.interfaceDao.RealEstateAgentDao
import com.example.realestatemanager.model.interfaceDao.RealEstateDao
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.utils.Converters


@Database(entities = [RealEstateAgent::class, RealEstate::class], version = 1,  exportSchema = false)
@TypeConverters(Converters::class)
abstract class MDataBase : RoomDatabase() {

    abstract fun realEstateAgentDao(): RealEstateAgentDao
    abstract fun realEstateDao(): RealEstateDao



    companion object {
        @Volatile
        private var INSTANCE: MDataBase? = null

        fun getDatabase(context: Context): MDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        MDataBase::class.java,
                        "real_estate_agency_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

     fun prepopulateDatabase(): Callback? {
        return object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val contentValues = ContentValues()
                contentValues.put("firstName", "Philippe")
                contentValues.put("lastName", "Dubois")
                contentValues.put("password", "azerty")
                contentValues.put("mail", "Philippe@gmail.com")

                db.insert("RealEstateAgent", OnConflictStrategy.IGNORE, contentValues)
            }
        }
    }

}