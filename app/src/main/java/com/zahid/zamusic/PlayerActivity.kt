package com.zahid.zamusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.zahid.zamusic.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var musicLisPA: ArrayList<Music>
        var songPosition: Int = 0
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var isPlaying = false
        var musicService:MusicService? = null
        var repeat:Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.coolPink)
        setContentView(binding.root)
        // For starting service
        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)
        initializeLayout()
        binding.playPauseBtnPA.setOnClickListener {
            if (isPlaying)
                pauseMusic()
            else
                playMusic()

        }
        binding.previousBtnPA.setOnClickListener { prevNextSong(false) }
        binding.nextBtnPA.setOnClickListener { prevNextSong(true) }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) musicService!!.mediaPlayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat){
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }else{
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
        }

    }
    private fun prevNextSong(increment:Boolean){
        if (increment){
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        }else{
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "MusicAdapter" -> {
                musicLisPA = ArrayList()
                musicLisPA.addAll(MainActivity.musicListMA)
                setLayout()
            }
            "MainActivity" -> {
                musicLisPA = ArrayList()
                musicLisPA.addAll(MainActivity.musicListMA)
                musicLisPA.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this).load(musicLisPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splash).centerCrop())
            .into(binding.songImgPA)
        binding.songNamePA.text = musicLisPA[songPosition].title
        if (repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) {
                musicService!!.mediaPlayer = MediaPlayer()
            }
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicLisPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            musicService?.showNotification()
            binding.tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)

        }catch (e:Exception){return}
    }
    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()


    }
    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()

        musicService?.seekSetup()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        }catch (e:Exception) {return}
    }

}