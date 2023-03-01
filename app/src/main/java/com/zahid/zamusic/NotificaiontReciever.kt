package com.zahid.zamusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(false, p0!!)
            ApplicationClass.PLAY -> Toast.makeText(p0,"Play is clicked",Toast.LENGTH_SHORT).show()
            ApplicationClass.NEXT -> prevNextSong(true,p0!!)
            ApplicationClass.EXIT -> {

                PlayerActivity.musicService?.stopForeground(true)
                PlayerActivity.musicService = null
                exitProcess(1)

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prevNextSong(increment:Boolean, context: Context){
        setSongPosition(increment = increment)
        PlayerActivity.musicService?.createMediaPlayer()
        Glide.with(context).load(PlayerActivity.musicLisPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash).centerCrop())
            .into(PlayerActivity.binding.songImgPA)
        PlayerActivity.binding.songNamePA.text = PlayerActivity.musicLisPA[PlayerActivity.songPosition].title
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService?.mediaPlayer?.start()
    }
}