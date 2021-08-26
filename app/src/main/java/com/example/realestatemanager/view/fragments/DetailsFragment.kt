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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityAddApartmentBinding
import com.example.realestatemanager.databinding.DialogCaptionPictureBinding
import com.example.realestatemanager.databinding.DialogSoldBinding
import com.example.realestatemanager.databinding.FragmentDetailsBinding
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.*
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.detailFragment.DetailFragmentVIewModel
import kotlin.collections.ArrayList

class DetailsFragment : Fragment() {

    var idRealEstateRetrieved: Int = -1
    private lateinit var detailFragmentViewModel: DetailFragmentVIewModel
    private lateinit var mContext: Context
    private var type: String = ""
    private var listPOI = ArrayList<String>()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = this.requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        if (arguments?.getInt(idRealEstate) != null) {
            idRealEstateRetrieved = arguments?.getInt(idRealEstate)!!
        } else Toast.makeText(mContext, "nope", Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (idRealEstateRetrieved != -1) {
            initViewModel()
        }
    }

    private fun initViewModel() {
        detailFragmentViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideDetailFragmentViewModelFactory(it)).get(
                DetailFragmentVIewModel::class.java
            )
        }!!


        detailFragmentViewModel.loadRealEstate(idRealEstateRetrieved)
            .observe(requireActivity(), {
                if (it != null) {
                    initElements(it)
                }
            })
    }

    private fun initElements(realEstate: RealEstate) {
        mRealEstate = realEstate
        imageList = mRealEstate.photoReference
        listCaption = mRealEstate.caption
        val fAddress = realEstate.numberStreet.toString() + " " + realEstate.address
        binding.addressTextviewDetails.text = fAddress
        binding.addressCountryTextviewDetails.text = realEstate.country
        binding.addressZipcodeTextviewDetails.text = realEstate.zipcode.toString()
        binding.addressCityTextviewDetails.text = realEstate.city
        binding.surfaceTextviewDetails.text = realEstate.surface.toString()
        binding.roomTextviewDetails.text = realEstate.roomNumber.toString()
        binding.fullDescription.text = realEstate.description
        var date = "Put in market the: " + realEstate.dateStart
        if (realEstate.sold) {
            date = date + " / Sold the: " + realEstate.dateEnd
            binding.dateOnMarket.text = date
        } else
            binding.dateOnMarket.text = date



        if (mRealEstate.listPOI.size != 0)
            binding.poiTitle.visibility = View.VISIBLE
        else
            binding.poiTitle.visibility = View.GONE

        binding.chipSchool.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.school))) View.VISIBLE else View.GONE
        binding.chipPark.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.park))) View.VISIBLE else View.GONE
        binding.chipBus.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.bus))) View.VISIBLE else View.GONE
        binding.chipStadium.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.stadium))) View.VISIBLE else View.GONE
        binding.chipSport.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.sport_club))) View.VISIBLE else View.GONE
        binding.chipPool.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.pool))) View.VISIBLE else View.GONE
        binding.chipSubway.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.subway))) View.VISIBLE else View.GONE
        binding.chipRestaurant.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.restaurant))) View.VISIBLE else View.GONE
        binding.chipMarket.visibility =
            if (mRealEstate.listPOI.contains(getString(R.string.market))) View.VISIBLE else View.GONE

        if (realEstate.numberBathroom != null && realEstate.numberBathroom != 0) {
            binding.bathroomTextviewDetailsTitle.visibility = View.VISIBLE
            binding.bathroomTextviewDetails.text = realEstate.numberBathroom.toString()
            binding.bathroomTextviewDetails.visibility = View.VISIBLE
        }
        if (realEstate.numberBedroom != null && realEstate.numberBedroom != 0) {
            binding.bedroomTextviewDetailsTitle.visibility = View.VISIBLE
            binding.bedroomTextviewDetails.text = realEstate.numberBedroom.toString()
            binding.bedroomTextviewDetails.visibility = View.VISIBLE
        }

        val separateAddress = realEstate.address.split(" ")
        var concenateAddress = ""
        for (i in separateAddress.indices) {
            concenateAddress = if (i != separateAddress.size - 1)
                concenateAddress + separateAddress[i] + "+"
            else concenateAddress + separateAddress[i]
        }

        Glide.with(mContext)
            .load(
                "https://maps.googleapis.com/maps/api/staticmap?markers=size:mid%7Ccolor:red%7C" + realEstate.numberStreet + "+" + concenateAddress
                        + "+" + realEstate.country + "&zoom=20&size=500x500&maptype=roadmap&key=" + mActivity.resources.getString(
                    R.string.google_maps_key
                )
            )
            .error(R.drawable.error_icons)
            .into(binding.imageviewMapsDetail)


        binding.linearlayoutDetailapartment.removeAllViewsInLayout()

        buildImages(
            realEstate.photoReference,
            realEstate.caption,
            null,
            binding.linearlayoutDetailapartment,
            false,
            null,
            null,
            mContext,
            requireActivity()
        )


        binding.modifyFloatingbutton.setOnClickListener { updateFloating() }
        binding.changesFloatingbutton.setOnClickListener {
            modifyRealEstate(mRealEstate)
            updateFloating()
        }
        binding.soldFloatingbutton.setOnClickListener {
            addDateSold(mRealEstate, mContext, detailFragmentViewModel)
            updateFloating()
        }
        binding.photoFloatingbutton.setOnClickListener {
            addPhoto()
            updateFloating()
        }
        binding.screenTransparent.setOnClickListener { binding.modifyFloatingbutton.callOnClick() }
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
                    buildDialogAfterGettingPhotos(bitmap)
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
                            buildDialogAfterGettingPhotos(bitmap)
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

    private fun buildDialogAfterGettingPhotos(bitmap: Bitmap?) {
        val dialogBuilder = AlertDialog.Builder(mContext)
        val bindingCaptionPictureBinding =
            DialogCaptionPictureBinding.inflate(LayoutInflater.from(mContext))
        val dialogView = bindingCaptionPictureBinding.root
        dialogBuilder.setView(dialogView)
        bindingCaptionPictureBinding.imageviewDialogCaption.setImageBitmap(bitmap)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val arrayType: Array<String> =
            requireActivity().resources.getStringArray(R.array.caption_picture)
        val mAdapter = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item, arrayType
        )
        bindingCaptionPictureBinding.spinnerDialogCaption.adapter = mAdapter
        bindingCaptionPictureBinding.spinnerDialogCaption.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
        bindingCaptionPictureBinding.saveButtonCaption.setOnClickListener {
            imageList = transformUriToString(bitmap, imageList)
            listCaption = addCaption(captionString, listCaption)
            if (bitmap != null) {
                mRealEstate.photoReference = imageList
                mRealEstate.caption = listCaption
                detailFragmentViewModel.updateRealEstate(mRealEstate)
            }
            alertDialog.dismiss()
        }
        bindingCaptionPictureBinding.cancelDialogCaption.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun updateFloating() {
        setVisibility(
            clicked,
            binding.changesFloatingbutton,
            binding.soldFloatingbutton,
            binding.photoFloatingbutton,
            binding.screenTransparent
        )
        setAnimation(clicked)
        clicked = !clicked
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

    private fun modifyRealEstate(mRealEstate: RealEstate) {
        val dialogBuilder = context?.let { AlertDialog.Builder(it) }
        bindingDialog = ActivityAddApartmentBinding.inflate(LayoutInflater.from(mContext))
        val dialogView = bindingDialog.root
        if (dialogBuilder != null) {
            dialogBuilder.setView(dialogView)
            val spinner: Spinner = dialogView.findViewById(R.id.spinner_type)

            bindingDialog.imageviewAddapartmentActivity.visibility = View.GONE
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
            detailFragmentViewModel.loadRealEstate(mRealEstate.id)
                .observe(requireActivity(), Observer {
                    if (it != null) {
                        bindingDialog.linearlayoutAddapartmentHidden.removeAllViewsInLayout()
                        buildImages(
                            it.photoReference,
                            it.caption,
                            bindingDialog.hiddenScrollviewAddapartment,
                            bindingDialog.linearlayoutAddapartmentHidden,
                            true,
                            detailFragmentViewModel,
                            it,
                            mContext,
                            requireActivity()
                        )
                    }
                })

            for (poi in mRealEstate.listPOI) {
                when (poi) {
                    getString(R.string.school) -> bindingDialog.school.isChecked = true
                    getString(R.string.park) -> bindingDialog.parc.isChecked = true
                    getString(R.string.bus) -> bindingDialog.Bus.isChecked = true
                    getString(R.string.stadium) -> bindingDialog.Stadium.isChecked = true
                    getString(R.string.sport_club) -> bindingDialog.Sport.isChecked = true
                    getString(R.string.pool) -> bindingDialog.pool.isChecked = true
                    getString(R.string.subway) -> bindingDialog.Metro.isChecked = true
                    getString(R.string.restaurant) -> bindingDialog.restaurant.isChecked = true
                    getString(R.string.market) -> bindingDialog.commerce.isChecked = true
                }
            }
            val alertDialog = dialogBuilder.create()
            alertDialog.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                    sold = mRealEstate.sold,
                    photoReference = mRealEstate.photoReference,
                    roomNumber = bindingDialog.editTextroom.text.toString().toInt(),
                    listPOI = listPOI,
                    dateStart = mRealEstate.dateStart,
                    dateEnd = mRealEstate.dateEnd,
                    caption = mRealEstate.caption,
                    numberBedroom = if (TextUtils.isEmpty(bindingDialog.edittextBedroom.text.toString())) null else bindingDialog.edittextBedroom.text.toString()
                        .toInt(),
                    numberBathroom = if (TextUtils.isEmpty(bindingDialog.edittextBathroom.text.toString())) null else bindingDialog.edittextBathroom.text.toString()
                        .toInt(),
                    day = mRealEstate.day,
                    month = mRealEstate.month,
                    year = mRealEstate.year
                )
                detailFragmentViewModel.updateRealEstate(apartment)
                alertDialog.dismiss()
            }
        }
    }

}