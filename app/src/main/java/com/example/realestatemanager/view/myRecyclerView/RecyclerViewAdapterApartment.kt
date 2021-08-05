package com.example.realestatemanager.view.myRecyclerView

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import java.lang.StringBuilder

class RecyclerViewAdapterApartment(
    context: Context,
    communicatorInterface: CommunicatorInterface
) :
    RecyclerView.Adapter<RecyclerViewAdapterApartment.ViewHolder>() {

    private var data: List<RealEstate> = ArrayList()
    private val context = context
    private val communicatorInterface = communicatorInterface

    fun updateApartmentList(list: List<RealEstate>) {
        this.data = list
        notifyDataSetChanged()
    }

    fun getObject(i: Int): RealEstate {
        return this.data[i]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.apartment_layout, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.address.text = data[position].address
        holder.price.text = "$ " + data[position].price.toString()
        holder.type.text = data[position].type
        val imageByteArray: ByteArray =
            Base64.decode(data[position].photoReference[0], Base64.DEFAULT)
        holder.pic.setImageBitmap(
            BitmapFactory.decodeByteArray(
                imageByteArray,
                0,
                imageByteArray.size
            )
        )
        holder.parent.setOnClickListener {
            communicatorInterface.passData(data[position].id)
        }
        if (data[position].sold) {
            holder.sold.visibility = View.VISIBLE
            holder.dateSold.visibility = View.VISIBLE
            val day: String = if (data[position].day!! <10) "0" + data[position].day.toString() else data[position].day.toString()
            val actualMonth = data[position].month?.plus(1)
            val month: String = if (actualMonth!! <10) "0$actualMonth" else actualMonth.toString()
            val date =day +"/" + month+"/" + data[position].year.toString()
            holder.dateSold.text = date
        }

        else {
            holder.sold.visibility = View.GONE
            holder.dateSold.visibility = View.GONE
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.image_apartment)
        val address: TextView = itemView.findViewById(R.id.textView_address_apartment)
        val price: TextView = itemView.findViewById(R.id.textView_price_apartment)
        val type: TextView = itemView.findViewById(R.id.textView_item_apartment)
        val parent: LinearLayout = itemView.findViewById(R.id.layout_appartment)
        val sold: ImageView = itemView.findViewById(R.id.sold_imageview)
        val dateSold: TextView = itemView.findViewById(R.id.date_sold_text)
    }
}