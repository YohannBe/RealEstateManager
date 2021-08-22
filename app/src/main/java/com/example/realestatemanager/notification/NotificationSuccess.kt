package com.example.realestatemanager.notification


import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.realestatemanager.R
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.view.activities.DetailsActivity

class NotificationSuccess : BroadcastReceiver() {
    private val channelId = "channel12365"

    override fun onReceive(context: Context?, intent: Intent?) {
        val response = intent?.getLongExtra(idRealEstate, -1)

        /*val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(idRealEstate, response)
        context?.startActivity(intent)*/
        buildNotification(context!!, response!!)

    }

    private fun buildNotification(context: Context, response: Long) {
        val notificationString = "Your apartment was added with success"
        val mIntent = Intent(context, DetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        mIntent.putExtra(idRealEstate, response)


        /*val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(mIntent)
            getPendingIntent(uniqueInt, PendingIntent.FLAG_UPDATE_CURRENT)
        }*/

        val pendingIntent =
            PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_ONE_SHOT)
        val inboxStyle = NotificationCompat.InboxStyle()

        inboxStyle.setBigContentTitle("Real estate Manager notification")
        inboxStyle.addLine(notificationString)


        val notificationBuilder =
            NotificationCompat.Builder(context, channelId)
                .setContentTitle("Real estate Manager")
                .setContentText("Don't forget your reservation !")
                .setSmallIcon(R.drawable.breakfeast_icons)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(inboxStyle)


        with(NotificationManagerCompat.from(context)) {
            notify(0, notificationBuilder.build())
        }
    }

    /* private fun createNotificationChannel() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val name = "real estate manager notification"
             val descriptionText = "real estate manager notification description"
             val importance = NotificationManager.IMPORTANCE_DEFAULT
             val channel = NotificationChannel(channelId, name, importance).apply {
                 description = descriptionText
             }
             val notificationManager: NotificationManager =
                 getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             notificationManager.createNotificationChannel(channel)
         }
     }*/

}