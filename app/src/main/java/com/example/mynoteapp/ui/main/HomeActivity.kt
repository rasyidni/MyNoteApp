package com.example.mynoteapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mynoteapp.R
import com.example.mynoteapp.databinding.ActivityHomeBinding
import com.example.mynoteapp.ui.insert.NoteAddUpdateActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "KPU"

        with(binding) {
            btnForm.setOnClickListener {
                val intent = Intent(this@HomeActivity, NoteAddUpdateActivity::class.java)
                startActivity(intent)
            }

            btnLihatdata.setOnClickListener {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
            }

            btnKeluar.setOnClickListener {
                finish()
            }

            btnInformasi.setOnClickListener {
                val intent = Intent(this@HomeActivity, InformationActivity::class.java)
                startActivity(intent)
            }
        }
    }
}