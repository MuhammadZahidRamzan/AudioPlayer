package com.zahid.zamusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zahid.zamusic.databinding.ActivityMainBinding
import com.zahid.zamusic.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setTheme(R.style.coolPink)
        setContentView(binding.root)
    }
}