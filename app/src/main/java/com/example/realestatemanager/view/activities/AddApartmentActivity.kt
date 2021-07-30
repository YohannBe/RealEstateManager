package com.example.realestatemanager.view.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityAddApartmentBinding
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.*
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddApartmentActivity : AppCompatActivity() {

    private var listPOI = ArrayList<String>()
    private var listCaption = ArrayList<String>()
    private var type: String = ""
    private var captionString: String = ""
    private var imageList: ArrayList<String> = ArrayList()
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE


    private lateinit var binding: ActivityAddApartmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddApartmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        val realEstateAgentViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )

        val idAgent = intent.getIntExtra(intentIdAgent, -1)


        val arrayType: Array<String> = resources.getStringArray(R.array.type)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayType
        )
        binding.spinnerType.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        binding.imageviewApartment.setOnClickListener {
            onClickAddFile()
        }

        binding.buttonSaveApartment.setOnClickListener {

            if (!TextUtils.isEmpty(type) &&
                !TextUtils.isEmpty(binding.editTextDescription.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextNumberPrice.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextNumberSurface.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextAddress.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextNumberAddress.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextzipcodeAddress.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextcity.text.toString()) &&
                !TextUtils.isEmpty(binding.editTextCountry.text.toString()) &&
                imageList.size != 0 &&
                !TextUtils.isEmpty(binding.editTextroom.text.toString())
            ) {
                listPOI = checkCheckButton(
                    binding.school,
                    binding.parc,
                    binding.Bus,
                    binding.Stadium,
                    binding.restaurant,
                    binding.commerce,
                    binding.Metro,
                    binding.pool,
                    binding.Sport,
                    listPOI
                )
                val apartment = RealEstate(
                    type = type,
                    description = (binding.editTextDescription.text.toString()),
                    price = binding.editTextNumberPrice.text.toString().toInt(),
                    surface = binding.editTextNumberSurface.text.toString().toInt(),
                    address = binding.editTextAddress.text.toString(),
                    country = binding.editTextCountry.text.toString(),
                    city = binding.editTextcity.text.toString(),
                    zipcode = binding.editTextzipcodeAddress.text.toString().toInt(),
                    numberStreet = binding.editTextNumberAddress.text.toString().toInt(),
                    iDRealEstateAgent = idAgent,
                    sold = false,
                    photoReference = imageList,
                    roomNumber = binding.editTextroom.text.toString().toInt(),
                    listPOI = listPOI,
                    dateStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    dateEnd = null,
                    caption = listCaption,
                    numberBedroom = if (TextUtils.isEmpty(binding.edittextBedroom.text.toString())) null else binding.edittextBedroom.text.toString()
                        .toInt(),
                    numberBathroom = if (TextUtils.isEmpty(binding.edittextBathroom.text.toString())) null else binding.edittextBathroom.text.toString()
                        .toInt()
                )
                realEstateAgentViewModel.insertApartment(apartment)
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("account", idAgent)
                startActivity(intent)
            } else Toast.makeText(this, "some fields are missing", Toast.LENGTH_SHORT).show()
        }
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
                                        contentResolver,
                                        it
                                    )
                                }
                                    ?.let { ImageDecoder.decodeBitmap(it) }!!
                            } else {
                                MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    clipData.getItemAt(i).uri
                                )
                            }
                            buildDialog(bitmap)
                        }
                    }
                }
            } else Toast.makeText(this, getString(R.string.no_photo_choosen), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun buildDialog(bitmap: Bitmap?) {
        val dialogBuilder = AlertDialog.Builder(this)
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

        val arrayType: Array<String> = resources.getStringArray(R.array.caption_picture)
        val mAdapter = ArrayAdapter(
            this,
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
            buildImageView(
                bitmap,
                binding.hiddenScrollviewAddapartment,
                this,
                binding.linearlayoutAddapartmentHidden,
                this
            )
            alertDialog.dismiss()
        }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    @AfterPermissionGranted(100)
    fun onClickAddFile() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(
                this,
                "file access",
                RC_IMAGE_PERMS,
                PERMS
            )
            return
        }
        val chooserIntent = createChooserIntent()
        startActivityForResult(chooserIntent, RC_CHOOSE_PHOTO)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        handleResponsePic(requestCode, resultCode, data!!)
    }
}