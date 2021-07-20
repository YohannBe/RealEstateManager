package com.example.realestatemanager.view.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddApartmentActivity : AppCompatActivity() {

    private lateinit var buttonSave: Button
    private lateinit var description: EditText
    private lateinit var price: EditText
    private lateinit var surface: EditText
    private lateinit var address: EditText
    private lateinit var picture: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var school: CheckBox
    private lateinit var park: CheckBox
    private lateinit var bus: CheckBox
    private lateinit var sport: CheckBox
    private lateinit var stadium: CheckBox
    private lateinit var pool: CheckBox
    private lateinit var subwyay: CheckBox
    private lateinit var restaurant: CheckBox
    private lateinit var market: CheckBox

    private var listPOI = ArrayList<String>()
    private var type: String = ""
    private var chosenPicture: Uri? = null
    private var imageByte: ByteArray? = null
    private val PERMS = Manifest.permission.READ_EXTERNAL_STORAGE
    private val RC_CHOOSE_PHOTO = 1234
    private val RC_IMAGE_PERMS = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_apartment)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        val realEstateAgentViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )

        val idAgent = intent.getIntExtra("idAgent", -1)
        description = findViewById(R.id.editTextDescription)
        price = findViewById(R.id.editTextNumberPrice)
        surface = findViewById(R.id.editTextNumberSurface)
        address = findViewById(R.id.editTextAddress)
        picture = findViewById(R.id.imageview_apartment)
        spinner = findViewById(R.id.spinner_type)
        school = findViewById(R.id.school)
        park = findViewById(R.id.parc)
        bus = findViewById(R.id.Bus)
        stadium = findViewById(R.id.Stadium)
        sport = findViewById(R.id.Sport)
        pool = findViewById(R.id.pool)
        subwyay = findViewById(R.id.Metro)
        restaurant = findViewById(R.id.restaurant)
        market = findViewById(R.id.commerce)

        val arrayType: Array<String> = resources.getStringArray(R.array.type)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayType
        )
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

        picture.setOnClickListener {
            onClickAddFile()
        }



        buttonSave = findViewById(R.id.button_save_apartment)
        buttonSave.setOnClickListener {

            if (!TextUtils.isEmpty(type) &&
                !TextUtils.isEmpty(description.text.toString()) &&
                !TextUtils.isEmpty(price.text.toString()) &&
                !TextUtils.isEmpty(surface.text.toString()) &&
                !TextUtils.isEmpty(address.text.toString()) &&
                imageByte != null
            ) {
                checkCheckButton()
                val apartment = RealEstate(
                    type = type,
                    description = description.text.toString(),
                    price = price.text.toString().toInt(),
                    surface = surface.text.toString().toInt(),
                    address = address.text.toString(),
                    iDRealEstateAgent = idAgent,
                    sold = false,
                    photoReference = imageByte!!,
                    roomNumber = 2,
                    listPOI = listPOI,
                    dateStart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    dateEnd = null
                )
                realEstateAgentViewModel.insertApartment(apartment)
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("account", idAgent)
                startActivity(intent)
            } else Toast.makeText(this, "some fields are missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCheckButton() {
        if (school.isChecked)
            listPOI.add(school.text.toString())
        else if (listPOI.contains(school.text.toString()))
            listPOI.remove(school.text.toString())
        if (bus.isChecked)
            listPOI.add(bus.text.toString())
        else if (listPOI.contains(bus.text.toString()))
            listPOI.remove(bus.text.toString())
        if (pool.isChecked)
            listPOI.add(pool.text.toString())
        else if (listPOI.contains(pool.text.toString()))
            listPOI.remove(pool.text.toString())
        if (sport.isChecked)
            listPOI.add(sport.text.toString())
        else if (listPOI.contains(sport.text.toString()))
            listPOI.remove(sport.text.toString())
        if (restaurant.isChecked)
            listPOI.add(restaurant.text.toString())
        else if (listPOI.contains(restaurant.text.toString()))
            listPOI.remove(restaurant.text.toString())
        if (stadium.isChecked)
            listPOI.add(stadium.text.toString())
        else if (listPOI.contains(stadium.text.toString()))
            listPOI.remove(stadium.text.toString())
        if (market.isChecked)
            listPOI.add(market.text.toString())
        else if (listPOI.contains(market.text.toString()))
            listPOI.remove(market.text.toString())
        if (subwyay.isChecked)
            listPOI.add(subwyay.text.toString())
        else if (listPOI.contains(subwyay.text.toString()))
            listPOI.remove(subwyay.text.toString())
        if (park.isChecked)
            listPOI.add(park.text.toString())
        else if (listPOI.contains(park.text.toString()))
            listPOI.remove(park.text.toString())
    }


    private fun handleResponsePic(
        requestCode: Int,
        resultCode: Int,
        data: Intent
    ) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                val bundle = data.extras
                val bitmap: Bitmap
                if (bundle?.get("data") != null) {
                    bitmap = bundle?.get("data") as Bitmap
                    picture.setImageBitmap(bitmap)
                } else {
                    chosenPicture = data.data
                    bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        data.data?.let { ImageDecoder.createSource(contentResolver, it) }
                            ?.let { ImageDecoder.decodeBitmap(it) }!!
                    } else {
                        MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            chosenPicture
                        )
                    }
                    picture.setImageBitmap(bitmap)
                }

                val objectOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, objectOutputStream)
                imageByte = objectOutputStream.toByteArray()

            } else Toast.makeText(this, getString(R.string.no_photo_choosen), Toast.LENGTH_SHORT)
                .show()
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

        val pickImageFileIntent = Intent()
        pickImageFileIntent.type = "image/*"
        pickImageFileIntent.action = Intent.ACTION_GET_CONTENT

        val intentPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val captureCameraImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val chooserIntent =
            Intent.createChooser(pickImageFileIntent, "Capture from camera or Select from gallery")
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            arrayOf(captureCameraImageIntent, intentPhoto)
        )
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