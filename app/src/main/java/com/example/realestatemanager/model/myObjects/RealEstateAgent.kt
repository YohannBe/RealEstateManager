package com.example.realestatemanager.model.myObjects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RealEstateAgent (
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "firstName")
        var firstName: String,
        @ColumnInfo(name = "lastName")
        var lastName: String,
        @ColumnInfo(name = "password")
        var password: String,
        @ColumnInfo(name = "mail")
        var mail: String
){
        constructor(firstName: String, lastName: String, password: String, mail: String): this(
                0, firstName, lastName, password, mail
        )
}