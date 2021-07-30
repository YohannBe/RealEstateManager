package com.example.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.CheckBox
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.realestatemanager.R
import java.io.ByteArrayOutputStream

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

fun createCanvas(photoReference: String, caption: String): Bitmap {
    val imageByteArray: ByteArray = Base64.decode(photoReference, Base64.DEFAULT)
    var bitmap = BitmapFactory.decodeByteArray(
        imageByteArray,
        0,
        imageByteArray.size
    )

    val paint = Paint()
    paint.color = Color.WHITE
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 60F
    val textRec = Rect()
    val overRect = Rect(0, 300, 500, 500)
    val mString = caption
    paint.getTextBounds(mString, 0, mString.length, textRec)
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
    val newBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)

    val paintOver = Paint()
    paintOver.color = Color.BLACK
    paintOver.alpha = 150

    val canvas = Canvas(newBitmap)

    canvas.drawBitmap(scaledBitmap, 0F, 0F, null)
    canvas.drawRect(overRect, paintOver)
    canvas.drawText(
        mString,
        textRec.width().toFloat(),
        textRec.height().toFloat() + 350,
        paint
    )
    return newBitmap
}


fun buildImageView(
    bitmap: Bitmap?,
    hiddenScrollView: HorizontalScrollView?,
    context: Context,
    hiddenLinearLayout: LinearLayout,
    activity: Activity
): LinearLayout {
    if (hiddenScrollView != null) {
        if (hiddenScrollView.visibility == View.GONE)
            hiddenScrollView.visibility = View.VISIBLE
    }
    val imageView = ImageView(context)
    val layoutParams = LinearLayout.LayoutParams(500, 500)
    layoutParams.marginEnd = 20
    imageView.layoutParams = layoutParams
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    imageView.setImageBitmap(bitmap)
    imageView.setOnClickListener {
        buildDialog(bitmap, activity, context)
    }
    hiddenLinearLayout.addView(imageView)

    return hiddenLinearLayout
}

fun buildDialog(bitmap: Bitmap?, activity: Activity, context: Context) {
    val dialogBuilder = context?.let { AlertDialog.Builder(it) }
    val inflater = activity.layoutInflater
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