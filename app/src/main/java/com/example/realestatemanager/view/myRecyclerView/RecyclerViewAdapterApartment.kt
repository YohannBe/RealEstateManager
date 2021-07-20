package com.example.realestatemanager.view.myRecyclerView

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate

class RecyclerViewAdapterApartment(context: Context) :
    RecyclerView.Adapter<RecyclerViewAdapterApartment.ViewHolder>() {

    private var data: List<RealEstate> = ArrayList()
    private val context = context

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
        holder.pic.setImageBitmap(
            BitmapFactory.decodeByteArray(
                data[position].photoReference,
                0,
                data[position].photoReference.size
            )
        )
        holder.parent.setOnClickListener {

            for (i in 1 until data[position].listPOI.size)
            Toast.makeText(context, data[position].listPOI[i], Toast.LENGTH_SHORT).show()

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.image_apartment)
        val address: TextView = itemView.findViewById(R.id.textView_address_apartment)
        val price: TextView = itemView.findViewById(R.id.textView_price_apartment)
        val type: TextView = itemView.findViewById(R.id.textView_item_apartment)
        val parent: LinearLayout = itemView.findViewById(R.id.layout_appartment)
    }
}