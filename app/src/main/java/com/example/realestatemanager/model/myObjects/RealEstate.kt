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
    var photoReference: ByteArray,
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "sold")
    var sold: Boolean,
    @ColumnInfo(name = "iDRealEstateAgent")
    var iDRealEstateAgent: Int,
    @ColumnInfo(name = "listPOI")
    var listPOI: ArrayList<String>,
    @ColumnInfo(name = "dateStart")
    var dateStart: String,
    @ColumnInfo(name = "dateEnd")
    var dateEnd: String?
) {
    constructor(
        type: String,
        price: Int,
        surface: Int,
        roomNumber: Int,
        description: String,
        photoReference: ByteArray,
        address: String,
        sold: Boolean,
        iDRealEstateAgent: Int,
        listPOI: ArrayList<String>,
        dateStart: String,
        dateEnd: String?
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
                sold,
                iDRealEstateAgent,
                listPOI,
                dateStart,
                dateEnd
            )
}
