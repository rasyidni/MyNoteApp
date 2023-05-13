package com.example.mynoteapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mynoteapp.R

class InformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        supportActionBar?.title = "Informasi"
    }
}