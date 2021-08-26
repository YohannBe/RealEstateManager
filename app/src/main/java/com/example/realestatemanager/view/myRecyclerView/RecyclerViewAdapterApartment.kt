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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.view.myInterface.CommunicatorInterface

class RecyclerViewAdapterApartment(
    context: Context,
    communicatorInterface: CommunicatorInterface
) :
    RecyclerView.Adapter<RecyclerViewAdapterApartment.ViewHolder>() {

    private var data: List<RealEstate> = ArrayList()
    private val context = context
    private val communicatorInterface = communicatorInterface
    private var clickedItem: Int = -1

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


        if (clickedItem == position){
            holder.address.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondaryLight))
        } else {
            holder.address.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.colorSecondary))
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBackground))
        }

        holder.parent.setOnClickListener {
            clickedItem = position
            notifyDataSetChanged()
            communicatorInterface.passData(data[position].id)
        }

        holder.sold.visibility = if (data[position].sold) View.VISIBLE else View.GONE
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.image_apartment)
        val address: TextView = itemView.findViewById(R.id.textView_address_apartment)
        val price: TextView = itemView.findViewById(R.id.textView_price_apartment)
        val type: TextView = itemView.findViewById(R.id.textView_item_apartment)
        val parent: LinearLayout = itemView.findViewById(R.id.layout_appartment)
        val sold: ImageView = itemView.findViewById(R.id.sold_imageview)
        val container: CardView = itemView.findViewById(R.id.cardview_apartment_layout_container)
    }
}