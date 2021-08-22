package com.example.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityAddApartmentBinding
import com.example.realestatemanager.databinding.ActivityProfileSettingBinding
import com.example.realestatemanager.databinding.DialogLoginBinding
import com.example.realestatemanager.databinding.DialogSoldBinding
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.view.activities.LogInActivity
import com.example.realestatemanager.view.activities.ProfileSettingActivity
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

/**
 * get list of photo, build image with caption for each element*/
fun buildImages(
    photoReference: ArrayList<String>,
    caption: ArrayList<String>,
    hiddenScrollView: HorizontalScrollView?,
    linearLayout: LinearLayout,
    modification: Boolean,
    realEstateAgentViewModel: RealEstateAgentViewModel?,
    realEstate: RealEstate?,
    context: Context,
    activity: Activity
) {
    for (i in 0 until photoReference.size) {
        val imageByteArray: ByteArray = Base64.decode(photoReference[i], Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(
            imageByteArray,
            0,
            imageByteArray.size
        )
        buildImageView(
            bitmap,
            hiddenScrollView,
            context,
            linearLayout,
            activity,
            caption[i],
            modification,
            realEstateAgentViewModel,
            realEstate,
            photoReference[i]
        )
    }
}

/**
 * get photo, add the newly build photo to layout*/

fun buildImageView(
    bitmap: Bitmap,
    hiddenScrollView: HorizontalScrollView?,
    context: Context,
    hiddenLinearLayout: LinearLayout,
    activity: Activity,
    caption: String?,
    modification: Boolean,
    realEstateAgentViewModel: RealEstateAgentViewModel?,
    realEstate: RealEstate?,
    photoReference: String?
) {
    if (hiddenScrollView != null) {
        if (hiddenScrollView.visibility == View.GONE)
            hiddenScrollView.visibility = View.VISIBLE
    }

    val imageView = ImageView(context)
    val layoutParams = LinearLayout.LayoutParams(500, 500)
    layoutParams.marginEnd = 20
    imageView.layoutParams = layoutParams
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    if (caption != null) {
        val newBitmap = createCanvas(bitmap, caption)
        imageView.setImageBitmap(newBitmap)
    } else
        imageView.setImageBitmap(bitmap)
    imageView.setOnClickListener {
        buildDialog(
            bitmap,
            activity,
            context,
            modification,
            realEstateAgentViewModel,
            realEstate,
            photoReference,
            caption
        )
    }
    hiddenLinearLayout.addView(imageView)
}

/**
 * get photo, add the caption on its canvas layout*/

fun createCanvas(bitmap: Bitmap, caption: String): Bitmap {

    val paint = Paint()
    paint.color = Color.WHITE
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 60F
    val textRec = Rect()
    val overRect = Rect(0, 300, 500, 500)
    paint.getTextBounds(caption, 0, caption.length, textRec)
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
    val newBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    val paintOver = Paint()
    paintOver.color = Color.BLACK
    paintOver.alpha = 150

    val canvas = Canvas(newBitmap)

    canvas.drawBitmap(scaledBitmap, 0F, 0F, null)
    canvas.drawRect(overRect, paintOver)
    canvas.drawText(
        caption,
        textRec.width().toFloat(),
        textRec.height().toFloat() + 350,
        paint
    )
    return newBitmap
}

/**
 * set visibility of floating button*/
fun setVisibility(
    clicked: Boolean,
    changesFloatingbutton: FloatingActionButton,
    soldFloatingbutton: FloatingActionButton,
    photoFloatingbutton: FloatingActionButton,
    screenTransparent: Button
) {
    if (!clicked) {
        changesFloatingbutton.visibility = View.VISIBLE
        soldFloatingbutton.visibility = View.VISIBLE
        photoFloatingbutton.visibility = View.VISIBLE
        screenTransparent.visibility = View.VISIBLE
    } else {
        changesFloatingbutton.visibility = View.INVISIBLE
        soldFloatingbutton.visibility = View.INVISIBLE
        photoFloatingbutton.visibility = View.INVISIBLE
        screenTransparent.visibility = View.GONE
    }
}

/**
 * build dialog with a calendar and set sold date for sold apartment*/
fun addDateSold(mRealEstate: RealEstate, context: Context, realEstateAgentViewModel: RealEstateAgentViewModel) {
    val dialogBuilder = context.let { AlertDialog.Builder(it) }
    val bindingSold = DialogSoldBinding.inflate(LayoutInflater.from(context))
    val dialogView = bindingSold.root

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
            realEstateAgentViewModel.updateRealEstate(mRealEstate)
            alertDialog.dismiss()
        }

}


















fun transformUriToString(bitmap: Bitmap?, imageList: ArrayList<String>): ArrayList<String> {
    val imageByte: ByteArray
    val objectOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, objectOutputStream)
    imageByte = objectOutputStream.toByteArray()
    imageList.add(Base64.encodeToString(imageByte, Base64.NO_WRAP))
    return imageList
}

fun addCaption(captionString: String, listCaption: ArrayList<String>): ArrayList<String> {
    listCaption.add(captionString)
    return listCaption
}

fun createChooserIntent(): Intent {
    val pickImageFileIntent = Intent()
    pickImageFileIntent.type = "image/*"
    pickImageFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    pickImageFileIntent.action = Intent.ACTION_GET_CONTENT

    val intentPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    val captureCameraImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    val chooserIntent =
        Intent.createChooser(pickImageFileIntent, "Capture from camera or Select from gallery")
    chooserIntent.putExtra(
        Intent.EXTRA_INITIAL_INTENTS,
        arrayOf(captureCameraImageIntent, intentPhoto)
    )
    return chooserIntent
}

fun checkCheckButton(
    school: CheckBox,
    park: CheckBox,
    bus: CheckBox,
    stadium: CheckBox,
    restaurant: CheckBox,
    market: CheckBox,
    subwyay: CheckBox,
    pool: CheckBox,
    sport: CheckBox,
    listPOI: ArrayList<String>
): ArrayList<String> {
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

    return listPOI
}


fun buildDialogRegistration(
    logInActivity: LogInActivity,
    mailEditText: EditText,
    passwordEditText: EditText,
    context: Context
) {
    val dialogBuilder = AlertDialog.Builder(logInActivity)
    val bindingDialog = DialogLoginBinding.inflate(LayoutInflater.from(context))
    val dialogView = bindingDialog.root
    dialogBuilder.setView(dialogView)
    val alertDialog = dialogBuilder.create()
    alertDialog.show()
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    bindingDialog.registerButtonDialog.setOnClickListener {
        val intent = Intent(logInActivity, ProfileSettingActivity::class.java)
        intent.putExtra("mail", mailEditText.text.toString())
        intent.putExtra("password", passwordEditText.text.toString())
        logInActivity.startActivity(intent)
    }
    bindingDialog.cancelButtonDialog.setOnClickListener {
        alertDialog.dismiss()
    }
}

fun buildDialog(
    bitmap: Bitmap?,
    activity: Activity,
    context: Context,
    modification: Boolean,
    realEstateAgentViewModel: RealEstateAgentViewModel?,
    realEstate: RealEstate?,
    photoReference: String?,
    caption: String?
) {
    val dialogBuilder = context.let { AlertDialog.Builder(it) }
    val inflater = activity.layoutInflater
    val dialogView = inflater.inflate(R.layout.dialog_picture, null)
    dialogBuilder.setView(dialogView)
    val mImage: ImageView = dialogView.findViewById(R.id.imageview_picture)
    val mDeleteCardView: CardView = dialogView.findViewById(R.id.container_delete_pic)
    val mDeleteButton: Button = dialogView.findViewById(R.id.button_delete_pic)
    mImage.setImageBitmap(bitmap)
    if (!modification)
        mDeleteCardView.visibility = View.GONE
    else
        mDeleteCardView.visibility = View.VISIBLE

    val alertDialog = dialogBuilder.create()
    alertDialog.show()
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    mDeleteButton.setOnClickListener {
        if (realEstate?.photoReference?.size!! > 1) {
            realEstate.photoReference.remove(photoReference)
            realEstate.caption.remove(caption)
            realEstateAgentViewModel?.updateRealEstate(realEstate)
            alertDialog.dismiss()
        } else
            Toast.makeText(context, "You have to keep at least one picture", Toast.LENGTH_SHORT)
                .show()
    }
}

fun getLocationByAddress(context: Context, strAddress: String?): LatLng? {
    val coder = Geocoder(context)
    try {
        val address = coder.getFromLocationName(strAddress, 1) ?: return null
        val location = address.first()
        return LatLng(location.latitude, location.longitude)
    } catch (e: Exception) {
        Log.d("GeoCoder exception", e.toString())
    }
    return null
}

