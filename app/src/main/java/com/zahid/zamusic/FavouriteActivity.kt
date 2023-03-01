package com.zahid.zamusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zahid.zamusic.databinding.ActivityFavouriteBinding
import com.zahid.zamusic.databinding.ActivityMainBinding

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)

        setTheme(R.style.coolPink)
        setContentView(binding.root)
    }
}