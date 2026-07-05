package com.mario.beta_antidh

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.mario.beta_antidh.databinding.ActivityMainBinding

// activty principale container fragment
class MainActivity : AppCompatActivity() { //appcompact default android che ereditiamo
    private lateinit var binding: ActivityMainBinding  //oggetto binding 
    
    override fun onCreate(savedInstanceState: Bundle?) { // onCreate aactivity
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)  //no titolo schermata app

  // inflate activity -> crea oggetti da xml 
        binding = ActivityMainBinding.inflate(layoutInflater) 
        setContentView(binding.root)

        supportActionBar?.hide() //hide acrion bar
    }
}
