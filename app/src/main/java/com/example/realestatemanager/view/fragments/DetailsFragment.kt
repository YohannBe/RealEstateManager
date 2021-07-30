package com.example.realestatemanager.view.fragments

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.FragmentDetailsBinding
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.buildImageView
import com.example.realestatemanager.utils.checkCheckButton
import com.example.realestatemanager.utils.createCanvas
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList

class DetailsFragment : Fragment() {

    var idRealEstateRetrieved: Int = -1

    private lateinit var realEstateViewModel: RealEstateAgentViewModel
    private lateinit var mContext: Context
    private var type: String = ""
    private var listPOI = ArrayList<String>()
    private var first = true

    private lateinit var binding: FragmentDetailsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        idRealEstateRetrieved = arguments?.getInt(idRealEstate)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initElements()
    }

    private fun initElements() {
        realEstateViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideViewModelFactory(it)).get(
                RealEstateAgentViewModel::class.java
            )
        }!!

        realEstateViewModel.loadRealEstate(idRealEstateRetrieved)
            .observe(requireActivity(), Observer {
                if (it != null) {
                    val mRealEstate = it
                    val fAddress = it.numberStreet.toString() + " " + it.address
                    binding.addressTextviewDetails.text = fAddress
                    binding.addressCountryTextviewDetails.text = it.country
                    binding.addressZipcodeTextviewDetails.text = it.zipcode.toString()
                    binding.addressCityTextviewDetails.text = it.city
                    binding.surfaceTextviewDetails.text = it.surface.toString()
                    binding.roomTextviewDetails.text = it.roomNumber.toString()
                    binding.fullDescription.text = it.description


                    if (it.numberBathroom != null && it.numberBathroom != 0) {
                        binding.bathroomTextviewDetailsTitle.visibility = View.VISIBLE
                        binding.bathroomTextviewDetails.text = it.numberBathroom.toString()
                        binding.bathroomTextviewDetails.visibility = View.VISIBLE
                    }
                    if (it.numberBedroom != null && it.numberBedroom != 0) {
                        binding.bedroomTextviewDetailsTitle.visibility = View.VISIBLE
                        binding.bedroomTextviewDetails.text = it.numberBedroom.toString()
                        binding.bedroomTextviewDetails.visibility = View.VISIBLE
                    }

                    val separateAddress = it.address.split(" ")
                    var concenateAddress = ""
                    for (i in separateAddress.indices) {
                        concenateAddress = if (i != separateAddress.size - 1)
                            concenateAddress + separateAddress[i] + "+"
                        else concenateAddress + separateAddress[i]
                    }

                    Glide.with(mContext)
                        .load(
                            "https://maps.googleapis.com/maps/api/staticmap?markers=size:mid%7Ccolor:red%7C" + it.numberStreet + "+" + concenateAddress
                                    + "+" + it.country + "&zoom=20&size=500x500&maptype=roadmap&key=" + resources.getString(
                                R.string.google_maps_key
                            )
                        )
                        .error(R.drawable.error_icons)
                        .into(binding.imageviewMapsDetail)


                    if (first)
                        buildImages(it.photoReference, it.caption)

                    first = false
                    binding.modifyFloatingbutton.setOnClickListener { modifyRealEstate(mRealEstate) }
                }
            })
    }

    private fun modifyRealEstate(mRealEstate: RealEstate) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_add_apartment, null)
        if (dialogBuilder != null) {
            dialogBuilder.setView(dialogView)
            val description: EditText = dialogView.findViewById(R.id.editTextDescription)
            val price: EditText = dialogView.findViewById(R.id.editTextNumberPrice)
            val surface: EditText = dialogView.findViewById(R.id.editTextNumberSurface)
            val address: EditText = dialogView.findViewById(R.id.editTextAddress)
            val numberAddress: EditText = dialogView.findViewById(R.id.editTextNumberAddress)
            val cityAddress: EditText = dialogView.findViewById(R.id.editTextcity)
            val zipCodeAddress: EditText = dialogView.findViewById(R.id.editTextzipcodeAddress)
            val countryAddress: EditText = dialogView.findViewById(R.id.editTextCountry)
            val picture: ImageView = dialogView.findViewById(R.id.imageview_apartment)
            val spinner: Spinner = dialogView.findViewById(R.id.spinner_type)
            val school: CheckBox = dialogView.findViewById(R.id.school)
            val park: CheckBox = dialogView.findViewById(R.id.parc)
            val bus: CheckBox = dialogView.findViewById(R.id.Bus)
            val stadium: CheckBox = dialogView.findViewById(R.id.Stadium)
            val sport: CheckBox = dialogView.findViewById(R.id.Sport)
            val pool: CheckBox = dialogView.findViewById(R.id.pool)
            val subwyay: CheckBox = dialogView.findViewById(R.id.Metro)
            val restaurant: CheckBox = dialogView.findViewById(R.id.restaurant)
            val market: CheckBox = dialogView.findViewById(R.id.commerce)
            val roomNumber: EditText = dialogView.findViewById(R.id.editTextroom)
            val hiddenScrollView: HorizontalScrollView =
                dialogView.findViewById(R.id.hiddenScrollview_addapartment)
            val hiddenLinearLayout: LinearLayout =
                dialogView.findViewById(R.id.linearlayout_addapartment_hidden)
            val bathroomNumber: EditText = dialogView.findViewById(R.id.edittext_bathroom)
            val bedroomNumber: EditText = dialogView.findViewById(R.id.edittext_bedroom)
            val updateButton: Button = dialogView.findViewById(R.id.button_save_apartment)

            description.setText(mRealEstate.description)
            surface.setText(mRealEstate.surface.toString())
            price.setText(mRealEstate.price.toString())
            address.setText(mRealEstate.address)
            cityAddress.setText(mRealEstate.city)
            numberAddress.setText(mRealEstate.numberStreet.toString())
            zipCodeAddress.setText(mRealEstate.zipcode.toString())
            countryAddress.setText(mRealEstate.country)
            roomNumber.setText(mRealEstate.roomNumber.toString())
            mRealEstate.numberBedroom?.let { bedroomNumber.setText(it.toString()) }
            mRealEstate.numberBathroom?.let { bathroomNumber.setText(it.toString()) }
            updateButton.text = "Update"

            val arrayType: Array<String> = resources.getStringArray(R.array.type)
            val adapter = context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item, arrayType
                )
            }
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    type = arrayType[position]
                }

            }

            for (poi in mRealEstate.listPOI) {
                when (poi) {
                    "school" -> school.isChecked = true
                    "parc" -> park.isChecked = true
                    "Bus" -> bus.isChecked = true
                    "Stadium" -> stadium.isChecked = true
                    "Sport club" -> sport.isChecked = true
                    "Pool" -> pool.isChecked = true
                    "Metro" -> subwyay.isChecked = true
                    "restaurant" -> restaurant.isChecked = true
                    "commerce" -> market.isChecked = true
                }
            }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            updateButton.setOnClickListener {
                listPOI = checkCheckButton(
                    school,
                    park,
                    bus,
                    stadium,
                    restaurant,
                    market,
                    subwyay,
                    pool,
                    sport,
                    listPOI
                )
                val apartment = RealEstate(
                    id = mRealEstate.id,
                    type = type,
                    description = description.text.toString(),
                    price = price.text.toString().toInt(),
                    surface = surface.text.toString().toInt(),
                    address = address.text.toString(),
                    country = countryAddress.text.toString(),
                    city = cityAddress.text.toString(),
                    zipcode = zipCodeAddress.text.toString().toInt(),
                    numberStreet = numberAddress.text.toString().toInt(),
                    iDRealEstateAgent = mRealEstate.iDRealEstateAgent,
                    sold = false,
                    photoReference = mRealEstate.photoReference,
                    roomNumber = roomNumber.text.toString().toInt(),
                    listPOI = listPOI,
                    dateStart = mRealEstate.dateStart,
                    dateEnd = null,
                    caption = mRealEstate.caption,
                    numberBedroom = if (TextUtils.isEmpty(bedroomNumber.text.toString())) null else bedroomNumber.text.toString()
                        .toInt(),
                    numberBathroom = if (TextUtils.isEmpty(bathroomNumber.text.toString())) null else bathroomNumber.text.toString()
                        .toInt()
                )
                realEstateViewModel.updateRealEstate(apartment)
                alertDialog.dismiss()
            }
        }
    }


    private fun buildImages(
        photoReference: ArrayList<String>,
        caption: ArrayList<String>
    ) {
        for (i in 0 until photoReference.size) {
            val imageByteArray: ByteArray = Base64.decode(photoReference[i], Base64.DEFAULT)
            var bitmap = BitmapFactory.decodeByteArray(
                imageByteArray,
                0,
                imageByteArray.size
            )

            context?.let {
                activity?.let { it1 ->
                    buildImageView(
                        bitmap,
                        null,
                        it,
                        binding.linearlayoutDetailapartment,
                        it1
                    )
                }

            }
        }
    }

    private fun buildDialog(bitmap: Bitmap?) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_picture, null)
        if (dialogBuilder != null) {
            dialogBuilder.setView(dialogView)
            val mImage: ImageView = dialogView.findViewById(R.id.imageview_picture)
            mImage.setImageBitmap(bitmap)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}