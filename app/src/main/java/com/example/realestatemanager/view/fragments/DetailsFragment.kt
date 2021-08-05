package com.example.realestatemanager.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityAddApartmentBinding
import com.example.realestatemanager.databinding.DialogSoldBinding
import com.example.realestatemanager.databinding.FragmentDetailsBinding
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.*
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import kotlin.collections.ArrayList

class DetailsFragment : Fragment() {

    var idRealEstateRetrieved: Int = -1

    private lateinit var realEstateViewModel: RealEstateAgentViewModel
    private lateinit var mContext: Context
    private var type: String = ""
    private var listPOI = ArrayList<String>()
    private var first = true
    private var imageList: ArrayList<String> = ArrayList()
    private var captionString: String = ""
    private var listCaption = ArrayList<String>()
    private lateinit var mRealEstate: RealEstate
    private lateinit var mActivity: Activity
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            mContext,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            mContext,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            mContext,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            mContext,
            R.anim.from_upp_anim
        )
    }
    private var clicked = false

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var bindingDialog: ActivityAddApartmentBinding
    private lateinit var bindingSold: DialogSoldBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = this.requireActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        if (arguments?.getInt(idRealEstate) != null) {
            idRealEstateRetrieved = arguments?.getInt(idRealEstate)!!
        } else Toast.makeText(mContext, "nope", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (idRealEstateRetrieved != -1) {
            initElements()
        }
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
                    mRealEstate = it
                    imageList = mRealEstate.photoReference
                    listCaption = mRealEstate.caption
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
                                    + "+" + it.country + "&zoom=20&size=500x500&maptype=roadmap&key=" + mActivity.resources.getString(
                                R.string.google_maps_key
                            )
                        )
                        .error(R.drawable.error_icons)
                        .into(binding.imageviewMapsDetail)


                    binding.linearlayoutDetailapartment.removeAllViewsInLayout()

                    buildImages(
                        it.photoReference,
                        it.caption,
                        null,
                        binding.linearlayoutDetailapartment,
                        false,
                        null,
                        null
                    )


                    binding.modifyFloatingbutton.setOnClickListener { updateFloating() }
                    binding.changesFloatingbutton.setOnClickListener {
                        modifyRealEstate(mRealEstate)
                        updateFloating()
                    }
                    binding.soldFloatingbutton.setOnClickListener {
                        addDateSold(mRealEstate)
                        updateFloating()
                    }
                    binding.photoFloatingbutton.setOnClickListener {
                        addPhoto()
                        updateFloating()
                    }
                    binding.screenTransparent.setOnClickListener { binding.modifyFloatingbutton.callOnClick() }
                }
            })
    }

    private fun addPhoto() {
        val chooserIntent = createChooserIntent()
        startActivityForResult(chooserIntent, RC_CHOOSE_PHOTO)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null)
            handleResponsePic(requestCode, resultCode, data)
        else
            Toast.makeText(mContext, "no photo chosen", Toast.LENGTH_SHORT).show()
    }

    private fun handleResponsePic(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                val bundle = data.extras
                var bitmap: Bitmap?
                if (bundle?.get("data") != null) {
                    bitmap = bundle.get("data") as Bitmap
                    buildDialog(bitmap)
                } else {
                    val clipData = data.clipData
                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                clipData.getItemAt(i).uri?.let {
                                    ImageDecoder.createSource(
                                        mContext.contentResolver,
                                        it
                                    )
                                }
                                    ?.let { ImageDecoder.decodeBitmap(it) }!!
                            } else {
                                MediaStore.Images.Media.getBitmap(
                                    mContext.contentResolver,
                                    clipData.getItemAt(i).uri
                                )
                            }
                            buildDialog(bitmap)
                        }
                    }
                }
            } else Toast.makeText(
                mContext,
                getString(R.string.no_photo_choosen),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun buildDialog(bitmap: Bitmap?) {
        val dialogBuilder = AlertDialog.Builder(mContext)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_caption_picture, null)
        dialogBuilder.setView(dialogView)
        val cancel: Button = dialogView.findViewById(R.id.cancel_dialog_caption)
        val register: Button = dialogView.findViewById(R.id.save_button_caption)
        val caption: Spinner = dialogView.findViewById(R.id.spinner_dialog_caption)
        val mPicture: ImageView = dialogView.findViewById(R.id.imageview_dialog_caption)
        mPicture.setImageBitmap(bitmap)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val arrayType: Array<String> =
            requireActivity().resources.getStringArray(R.array.caption_picture)
        val mAdapter = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item, arrayType
        )
        caption.adapter = mAdapter

        caption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                captionString = arrayType[position]
            }
        }

        register.setOnClickListener {
            imageList = transformUriToString(bitmap, imageList)
            listCaption = addCaption(captionString, listCaption)
            if (bitmap != null) {
                mRealEstate.photoReference = imageList
                mRealEstate.caption = listCaption
                realEstateViewModel.updateRealEstate(mRealEstate)
            }
            alertDialog.dismiss()
        }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun updateFloating() {

        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked

    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.changesFloatingbutton.visibility = View.VISIBLE
            binding.soldFloatingbutton.visibility = View.VISIBLE
            binding.photoFloatingbutton.visibility = View.VISIBLE
            binding.screenTransparent.visibility = View.VISIBLE
        } else {
            binding.changesFloatingbutton.visibility = View.INVISIBLE
            binding.soldFloatingbutton.visibility = View.INVISIBLE
            binding.photoFloatingbutton.visibility = View.INVISIBLE
            binding.screenTransparent.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.modifyFloatingbutton.startAnimation(rotateOpen)
            binding.soldFloatingbutton.startAnimation(fromBottom)
            binding.changesFloatingbutton.startAnimation(fromBottom)
            binding.photoFloatingbutton.startAnimation(fromBottom)
        } else {
            binding.modifyFloatingbutton.startAnimation(rotateClose)
            binding.soldFloatingbutton.startAnimation(toBottom)
            binding.changesFloatingbutton.startAnimation(toBottom)
            binding.photoFloatingbutton.startAnimation(toBottom)
        }
    }

    private fun addDateSold(mRealEstate: RealEstate) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        bindingSold = DialogSoldBinding.inflate(LayoutInflater.from(mContext))
        val dialogView = bindingSold.root

        if (dialogBuilder != null) {
            dialogBuilder.setView(dialogView)
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bindingSold.cancelButtonDialogSold.setOnClickListener {
                alertDialog.dismiss()
            }
            bindingSold.saveButtonDialogSold.setOnClickListener {
                mRealEstate.sold = true
                mRealEstate.day = bindingSold.calendarSoldApartment.dayOfMonth
                mRealEstate.month = bindingSold.calendarSoldApartment.month
                mRealEstate.year = bindingSold.calendarSoldApartment.year
                realEstateViewModel.updateRealEstate(mRealEstate)
                alertDialog.dismiss()
            }
        }
    }

    private fun modifyRealEstate(mRealEstate: RealEstate) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        bindingDialog = ActivityAddApartmentBinding.inflate(LayoutInflater.from(mContext))
        val dialogView = bindingDialog.root
        if (dialogBuilder != null) {
            dialogBuilder.setView(dialogView)
            val spinner: Spinner = dialogView.findViewById(R.id.spinner_type)

            bindingDialog.editTextDescription.setText(mRealEstate.description)
            bindingDialog.editTextNumberSurface.setText(mRealEstate.surface.toString())
            bindingDialog.editTextNumberPrice.setText(mRealEstate.price.toString())
            bindingDialog.editTextAddress.setText(mRealEstate.address)
            bindingDialog.editTextcity.setText(mRealEstate.city)
            bindingDialog.editTextNumberAddress.setText(mRealEstate.numberStreet.toString())
            bindingDialog.editTextzipcodeAddress.setText(mRealEstate.zipcode.toString())
            bindingDialog.editTextCountry.setText(mRealEstate.country)
            bindingDialog.editTextroom.setText(mRealEstate.roomNumber.toString())
            mRealEstate.numberBedroom?.let { bindingDialog.edittextBedroom.setText(it.toString()) }
            mRealEstate.numberBathroom?.let { bindingDialog.edittextBathroom.setText(it.toString()) }
            bindingDialog.buttonSaveApartment.text = "Update"

            val arrayType: Array<String> = requireActivity().resources.getStringArray(R.array.type)
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

            bindingDialog.imageviewApartment.visibility = View.GONE
            realEstateViewModel.loadRealEstate(mRealEstate.id).observe(requireActivity(), Observer {
                if (it != null) {
                    bindingDialog.linearlayoutAddapartmentHidden.removeAllViewsInLayout()
                    buildImages(
                        it.photoReference,
                        it.caption,
                        bindingDialog.hiddenScrollviewAddapartment,
                        bindingDialog.linearlayoutAddapartmentHidden,
                        true,
                        realEstateViewModel,
                        it
                    )
                }
            })


            for (poi in mRealEstate.listPOI) {
                when (poi) {
                    "school" -> bindingDialog.school.isChecked = true
                    "parc" -> bindingDialog.parc.isChecked = true
                    "Bus" -> bindingDialog.Bus.isChecked = true
                    "Stadium" -> bindingDialog.Stadium.isChecked = true
                    "Sport club" -> bindingDialog.Sport.isChecked = true
                    "Pool" -> bindingDialog.pool.isChecked = true
                    "Metro" -> bindingDialog.Metro.isChecked = true
                    "restaurant" -> bindingDialog.restaurant.isChecked = true
                    "commerce" -> bindingDialog.commerce.isChecked = true
                }
            }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            bindingDialog.buttonSaveApartment.setOnClickListener {
                listPOI = checkCheckButton(
                    bindingDialog.school,
                    bindingDialog.parc,
                    bindingDialog.Bus,
                    bindingDialog.Stadium,
                    bindingDialog.restaurant,
                    bindingDialog.commerce,
                    bindingDialog.Metro,
                    bindingDialog.pool,
                    bindingDialog.Sport,
                    listPOI
                )
                val apartment = RealEstate(
                    id = mRealEstate.id,
                    type = type,
                    description = bindingDialog.editTextDescription.text.toString(),
                    price = bindingDialog.editTextNumberPrice.text.toString().toInt(),
                    surface = bindingDialog.editTextNumberSurface.text.toString().toInt(),
                    address = bindingDialog.editTextAddress.text.toString(),
                    country = bindingDialog.editTextCountry.text.toString(),
                    city = bindingDialog.editTextcity.text.toString(),
                    zipcode = bindingDialog.editTextzipcodeAddress.text.toString().toInt(),
                    numberStreet = bindingDialog.editTextNumberAddress.text.toString().toInt(),
                    iDRealEstateAgent = mRealEstate.iDRealEstateAgent,
                    sold = false,
                    photoReference = mRealEstate.photoReference,
                    roomNumber = bindingDialog.editTextroom.text.toString().toInt(),
                    listPOI = listPOI,
                    dateStart = mRealEstate.dateStart,
                    dateEnd = null,
                    caption = mRealEstate.caption,
                    numberBedroom = if (TextUtils.isEmpty(bindingDialog.edittextBedroom.text.toString())) null else bindingDialog.edittextBedroom.text.toString()
                        .toInt(),
                    numberBathroom = if (TextUtils.isEmpty(bindingDialog.edittextBathroom.text.toString())) null else bindingDialog.edittextBathroom.text.toString()
                        .toInt(),
                    day = mRealEstate.day,
                    month = mRealEstate.month,
                    year = mRealEstate.year
                )
                realEstateViewModel.updateRealEstate(apartment)
                alertDialog.dismiss()
            }
        }
    }


    private fun buildImages(
        photoReference: ArrayList<String>,
        caption: ArrayList<String>,
        hiddenScrollView: HorizontalScrollView?,
        linearLayout: LinearLayout,
        modification: Boolean,
        realEstateAgentViewModel: RealEstateAgentViewModel?,
        realEstate: RealEstate?
    ) {
        for (i in 0 until photoReference.size) {
            val imageByteArray: ByteArray = Base64.decode(photoReference[i], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(
                imageByteArray,
                0,
                imageByteArray.size
            )
            context?.let {
                activity?.let { it1 ->
                    buildImageView(
                        bitmap,
                        hiddenScrollView,
                        it,
                        linearLayout,
                        it1,
                        caption[i],
                        modification,
                        realEstateAgentViewModel,
                        realEstate,
                        photoReference[i]
                    )
                }

            }
        }
    }


}