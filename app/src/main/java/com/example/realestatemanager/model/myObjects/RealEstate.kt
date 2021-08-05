package com.example.realestatemanager.model.myObjects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = RealEstateAgent::class,
        parentColumns = ["id"],
        childColumns = ["iDRealEstateAgent"],
        onDelete = CASCADE
    )]
)
class RealEstate(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "price")
    var price: Int,
    @ColumnInfo(name = "surface")
    var surface: Int,
    @ColumnInfo(name = "roomNumber")
    var roomNumber: Int,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "photoReference")
    var photoReference: ArrayList<String>,
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "country")
    var country: String,
    @ColumnInfo(name = "zipcode")
    var zipcode: Int,
    @ColumnInfo(name = "numberStreet")
    var numberStreet: Int,
    @ColumnInfo(name = "sold")
    var sold: Boolean,
    @ColumnInfo(name = "day")
    var day: Int?,
    @ColumnInfo(name = "month")
    var month: Int?,
    @ColumnInfo(name = "year")
    var year: Int?,
    @ColumnInfo(name = "iDRealEstateAgent")
    var iDRealEstateAgent: Int,
    @ColumnInfo(name = "listPOI")
    var listPOI: ArrayList<String>,
    @ColumnInfo(name = "dateStart")
    var dateStart: String,
    @ColumnInfo(name = "dateEnd")
    var dateEnd: String?,
    @ColumnInfo(name = "caption")
    var caption: ArrayList<String>,
    @ColumnInfo(name = "numberBedroom")
    var numberBedroom: Int?,
    @ColumnInfo(name = "numberBathroom")
    var numberBathroom: Int?
) {
    constructor(
        type: String,
        price: Int,
        surface: Int,
        roomNumber: Int,
        description: String,
        photoReference: ArrayList<String>,
        address: String,
        city: String,
        country: String,
        zipcode: Int,
        numberStreet: Int,
        sold: Boolean,
        iDRealEstateAgent: Int,
        listPOI: ArrayList<String>,
        dateStart: String,
        dateEnd: String?,
        caption: ArrayList<String>,
        numberBedroom: Int?,
        numberBathroom: Int?,
        day: Int?,
        month: Int?,
        year: Int?
    ) :
            this(
                0,
                type,
                price,
                surface,
                roomNumber,
                description,
                photoReference,
                address,
                city,
                country,
                zipcode,
                numberStreet,
                sold,
                day,
                month,
                year,
                iDRealEstateAgent,
                listPOI,
                dateStart,
                dateEnd,
                caption,
                numberBedroom,
                numberBathroom
            )
}
