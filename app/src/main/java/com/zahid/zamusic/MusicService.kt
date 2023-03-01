package com.zahid.zamusic

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.zahid.zamusic.ApplicationClass.Companion.CHANNEL_ID

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification() {
        val intent = Intent(baseContext,PlayerActivity::class.java)
        intent.putExtra("index",PlayerActivity.songPosition)
        intent.putExtra("class","MusicAdapter")
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val prevIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val playIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val exitIntent = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val imageArt = getImgArt(PlayerActivity.musicLisPA[PlayerActivity.songPosition].path)
        val image = if (imageArt!= null){
            BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }else{
            BitmapFactory.decodeResource(resources,R.drawable.splash)

        }
        val notification: Notification = Notification.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(PlayerActivity.musicLisPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicLisPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.play_list)
            .setLargeIcon(image)
            .setPriority(Notification.PRIORITY_HIGH)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon,"Previous",prevPendingIntent)
            .addAction(R.drawable.next_icon,"Next",nextPendingIntent)
            .addAction(R.drawable.exit_icon,"Exit",exitPendingIntent)
            .addAction(R.drawable.play_icon,"Play",playPendingIntent)
            .build()
        startForeground(123,notification)
    }

    fun seekSetup() {
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createMediaPlayer(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) {
                PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            }
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicLisPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService?.showNotification()

        }catch (e:Exception){return}
    }
}